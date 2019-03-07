package com.example.a96653.LetsCode;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.graphics.Bitmap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;


public class certificate extends AppCompatActivity {
    TextView t ;
    ImageButton homebtn9;
    ImageView SHARE_certificate;
    static MySQLliteHelper mySqliteOpenHelper;
    static Bitmap bm,bmp2;
    ImageView imageView2 ;
    ImageView A;//فقط للتشييك
    public static Intent shareintent=new Intent (Intent.ACTION_SEND);
    public static Intent shareintent2=new Intent (Intent.ACTION_SEND);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);
        mySqliteOpenHelper=new MySQLliteHelper(this);
        //HOME BUTTON
       homebtn9=(ImageButton)findViewById(R.id.homebtn_certificate);
        homebtn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gohome=new Intent (getApplicationContext(),MainActivity.class);
                startActivity(gohome);
            }
        });
        //TAKING CHILD'S NAME FROM DB
        TextView nameView=(TextView) findViewById(R.id.childName);
        nameView.setText(  mySqliteOpenHelper. getChildName());
        if (getIntent().hasExtra("com.example.a96653.LetsCode")){
            String t=getIntent().getExtras().getString("com.example.a96653.LetsCode");
            nameView.setText(t);
        }
        //GET CHILD'S SCORE
        TextView textView = (TextView)findViewById(R.id.CHILD_SCORE);
        textView.setText(mySqliteOpenHelper.getChildScore()+"" );
        //CHANGING DATE
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c);
        TextView date = (TextView)findViewById(R.id.date);
        date.setText(formattedDate );

    //CREATING TABLE FOR PICTURE .
       String sql="CREATE TABLE IF NOT EXISTS PIC( pic MEDIUMBLOB )";
        mySqliteOpenHelper.queryData(sql);
        //TAKING SCREENSHOT .
        A=findViewById(R.id.BACK);
        A.setVisibility(View.INVISIBLE);
        SHARE_certificate=findViewById(R.id.SHARE_certificate);
        final View layout= findViewById(R.id.root_content);
        layout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY| View.SYSTEM_UI_FLAG_FULLSCREEN );
        imageView2 = (ImageView) findViewById(R.id.stamp);

        layout.post(new Runnable() {
            @Override
            public void run() {
                //TO REMOVE SYSTEM BORDERS
                layout.setBackgroundColor(Color.TRANSPARENT);
                homebtn9.setVisibility(View.INVISIBLE);
                SHARE_certificate.setVisibility(View.INVISIBLE);
                bm=getScreenShot(layout);
                 homebtn9.setVisibility(View.VISIBLE);
                SHARE_certificate.setVisibility(View.VISIBLE);
               layout.setSystemUiVisibility(View.VISIBLE);
                layout.setBackgroundResource(R.drawable.back);
                if (bm != null) {
                    retrieveImage( );//show bitmap over imageview

                } else {
               t= findViewById(R.id.textView26);
                    t.setText("NONO");}

            }
        });


        //Onclick for Share
        shareintent2=ShareCertificare();

        startActivity(Intent.createChooser(shareintent2,"مشاركة الشهادة مع : "));


    }//end of onCreate .

    /*  Show screenshot Bitmap */
    private void retrieveImage( ) {
        try{

            Cursor cursor=mySqliteOpenHelper.getData();
            cursor.moveToLast();
            byte[] image= cursor.getBlob(cursor.getColumnIndex("pic"));

            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            bmp2=bmp;
            imageView2.setImageBitmap(bmp);

            Toast.makeText(getApplicationContext(),"added",Toast.LENGTH_SHORT).show();

        }catch (Exception e){e.printStackTrace();   Toast.makeText(getApplicationContext(),"not added",Toast.LENGTH_SHORT).show();}




    }




public static Bitmap getScreenShot(View view){
        View screenView=view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap=Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);

    bm=bitmap;
    try{mySqliteOpenHelper.insertData(ImageToByte());

    } catch (Exception e){e.printStackTrace();}
    return bitmap;

    }


    private static byte[] ImageToByte(){


        Bitmap icon = bm;

        ByteArrayOutputStream stream= new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray=stream.toByteArray();
        return byteArray;
    }


public static Intent ShareCertificare (){


    shareintent.setType("image/jpg");
    ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
    bmp2.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
    File file =new File(Environment.getExternalStorageDirectory()+File.separator+"ImageDemo.jpg");
    //File file =new File(Environment.getExternalStorageDirectory().toString() + "/" +"Hey.jpg");
    try{
        file.createNewFile();
        FileOutputStream fileOutputStream=new FileOutputStream(file);
        fileOutputStream.write(byteArrayOutputStream.toByteArray());
        bmp2.compress(Bitmap.CompressFormat.JPEG,90,fileOutputStream);
    }catch (IOException e) {
        e.printStackTrace();
    }
    shareintent.putExtra(Intent.EXTRA_TEXT,"لقد أتممت جميع مهماتي مع تطبيق هيا نبرمج .");
    shareintent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+file.getAbsolutePath()));
    shareintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    return shareintent ;

}
}
