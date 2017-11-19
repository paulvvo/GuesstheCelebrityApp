package com.example.sleepy.guessthecelebrityapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {

    Button ansButton0,ansButton1, ansButton2, ansButton3;
    ImageView imageView;
    TextView scoreTextView,rightwrongTextView;
    ArrayList<String> pictureList;
    ArrayList<String> nameList;
    AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
    Random rand;
    int randomNumber, randomPersonNumber, scoreNum, scoreDeno;

    public class BackgroundTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    char current = (char) data;
                    result = result + current;
                    data = reader.read();
                }
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Done";
        }
    }

    public class BackgroundTask2 extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream  = connection.getInputStream();
                Bitmap downloadedBitmap = BitmapFactory.decodeStream(inputStream);
                return downloadedBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rand = new Random();
        int scoreNum = 0;
        int scoreDeno = 0;

        ansButton0 = (Button) findViewById(R.id.ansButton0);
        ansButton1 = (Button) findViewById(R.id.ansButton1);
        ansButton2 = (Button) findViewById(R.id.ansButton2);
        ansButton3 = (Button) findViewById(R.id.ansButton3);

        imageView = (ImageView) findViewById(R.id.imageView);
        scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        rightwrongTextView = (TextView) findViewById(R.id.rightwrongTextView);

        String result = null;
        BackgroundTask task = new BackgroundTask();

        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Pattern p = Pattern.compile("<img src=\"(.*?)\"");
        Matcher m = p.matcher(result);
        pictureList = new ArrayList<String>();

        while(m.find()){
            pictureList.add(m.group(1));
        }

        Pattern p2 = Pattern.compile("alt=\"(.*?)\"/>");
        Matcher m2 = p2.matcher(result);
        nameList = new ArrayList<String>();

        while(m2.find()){
            nameList.add(m2.group(1));
        }

        setUI();

    }

    public void setUI(){
        Bitmap downloadedImage;
        randomNumber = rand.nextInt(4);
        randomPersonNumber = rand.nextInt(100);
        ArrayList <Button> buttonList = new ArrayList<Button>(asList(ansButton0, ansButton1, ansButton2, ansButton3));
        BackgroundTask2 task2 = new BackgroundTask2();
        try {
            downloadedImage = task2.execute(pictureList.get(randomPersonNumber)).get();
            imageView.setImageBitmap(downloadedImage);

            for(int i=0; i<buttonList.size(); i++){
                if(i==randomNumber){
                    buttonList.get(i).setText(nameList.get(randomPersonNumber));
                }
                else{
                    buttonList.get(i).setText(nameList.get(rand.nextInt(100)));
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void submitAnswer(View view){

        if(randomNumber == Integer.parseInt(view.getTag().toString())){
            System.out.println("correct");
            rightwrongTextView.setVisibility(View.VISIBLE);
            rightwrongTextView.setText("CORRECT");
            scoreNum++;
            scoreDeno++;
        }
        else{
            rightwrongTextView.setVisibility(View.VISIBLE);
            rightwrongTextView.setText("INCORRECT");
            scoreDeno++;
            System.out.println("incorrect");
        }
        scoreTextView.setText(scoreNum + "/" + scoreDeno);
        setUI();
    }
}
