/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.androidtown.sns_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.androidtown.sns_project.view.Camera2BasicFragment;
import org.androidtown.sns_project.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import androidx.appcompat.app.AppCompatActivity;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";// 로그찍을때 태그
    private Camera2BasicFragment camera2BasicFragment;

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() { // 사진을 찍고 해당 사진을 읽어오는 리스너 --> Camera2BasicFragment 로 보내줌

        @Override
        public void onImageAvailable(ImageReader reader) {
            //mBackgroundHandler.post(new Camera2BasicFragment.ImageUpLoader(reader.acquireNextImage()));
            Log.v(TAG, "캡처성공 ");
            //리더 파일을 통해 파일을 저장시킬 것

            Image mImage = reader.acquireNextImage(); // 이미지 받아오고
            File mFile = new File(getExternalFilesDir(null), "profileImage.jpg"); // 받아온 이미지를 저장한다.

            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            Intent resultIntent=new Intent();
            resultIntent.putExtra("profilePath", mFile.toString());
            Log.v(TAG, "(CameraActivity)profilePath : "+mFile.toString());
            setResult(Activity.RESULT_OK,resultIntent);  // --> MemberinitActivity의 onActivityResult로 값을 보내줌
            camera2BasicFragment.closeCamera(); // 카메라 종료 시켜줌
            finish();//파일 값을 보내준 뒤 CameraActivity 종료
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Log.v(TAG, "Create");
        ////////////////////////////////////////////////////////////////////////////////////////////
        if (null == savedInstanceState) {

            Log.v(TAG, "savedInstanceState : "+savedInstanceState);
            camera2BasicFragment=new Camera2BasicFragment();
            camera2BasicFragment.setOnImageAvailableListener(mOnImageAvailableListener);// 사진을 찍고 해당 사진을 읽어오는 리스너 --> Camera2BasicFragment 로 보내줌
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, camera2BasicFragment)
                    .commit();

        }
        ////////////////////////////////////////////////////////////////////////////////////////////
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume/ 다른 액티비티 보임");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
