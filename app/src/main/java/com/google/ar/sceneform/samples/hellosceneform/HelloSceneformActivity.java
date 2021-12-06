/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.sceneform.samples.hellosceneform;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;
import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.animation.ModelAnimator;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.QuaternionEvaluator;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.AnimationData;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.schemas.lull.AxisSystem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class HelloSceneformActivity extends AppCompatActivity {
  private static final String TAG = HelloSceneformActivity.class.getSimpleName();
  private static final double MIN_OPENGL_VERSION = 3.0;

  private ArFragment arFragment;
  private ModelRenderable andyRenderable;
    Scene scene;
    //测量长度
    private List<Node> sphereNodeArray=new ArrayList<>();
    private List<AnchorInfoBean> dataArray = new ArrayList<>();
    private List<Node> startNodeArray = new ArrayList<>();
    private List<Node> endNodeArray = new ArrayList<>();
    private List<Node> lineNodeArray = new ArrayList<>();
  @Override
  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  // CompletableFuture requires api level 24
  // FutureReturnValueIgnored is not valid
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (!checkIsSupportedDeviceOrFinish(this)) {
      return;
    }

    setContentView(R.layout.activity_ux);
    arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

    // When you build a Renderable, Sceneform loads its resources in the background while returning
    // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
    ModelRenderable.builder()
        .setSource(this, R.raw.andy)
        .build()
        .thenAccept(renderable -> andyRenderable = renderable)
        .exceptionally(
            throwable -> {
              Toast toast =
                  Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
              toast.setGravity(Gravity.CENTER, 0, 0);
              toast.show();
              return null;
            });

        new Thread(){}.start();
      ArSceneView sceneView = arFragment.getArSceneView();

      // This is important to make sure that the camera stream renders first so that
      // the face mesh occlusion works correctly.
      sceneView.setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);

      scene = sceneView.getScene();

      scene.addOnUpdateListener(new Scene.OnUpdateListener() {
          @Override
          public void onUpdate(FrameTime frameTime) {


              Frame arFrame = arFragment.getArSceneView().getArFrame();
              float[] rotationQuaternion = arFrame.getCamera().getPose().getRotationQuaternion();


//           Anchor anchor = frameTime
//           AnchorNode anchorNode = new AnchorNode(anchor);
//           anchorNode.setParent(arFragment.getArSceneView().getScene());

          // Create the transformable andy and add it to the anchor.
//          TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
////              Node andy = new Node();
//          andy.setParent(scene);
//          andy.setRenderable(andyRenderable);
//          andy.select();
          }
      });



    arFragment.setOnTapArPlaneListener(
        (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
          if (andyRenderable == null) {
            return;
          }
//            showLength(hitResult);
//           Create the Anchor.  放置自定义3D模型
            showCustom3DModle(hitResult);
        });
  }

    private void showLength(HitResult hitResult) {
        AnchorInfoBean anchorInfoBean = new AnchorInfoBean("", hitResult.createAnchor(), 0.0);
        dataArray.add(anchorInfoBean);
        if (sphereNodeArray.size()>1){
            Anchor endAnchor = dataArray.get(dataArray.size() - 1).anchor;
            Anchor startAnchor = dataArray.get(dataArray.size() - 2).anchor;
            Pose endAnchorPoses = endAnchor.getPose();
            Pose startAnchorPose = startAnchor.getPose();

            float dx=startAnchorPose.tx()- endAnchorPoses.tx();
            float dy=startAnchorPose.ty()- endAnchorPoses.ty();
            float dz=startAnchorPose.tz()- endAnchorPoses.tz();
            anchorInfoBean.length = Math.sqrt((dx * dx + dy * dy + dz * dz));
            drawLine(startAnchor, endAnchor, anchorInfoBean.length);

        }else {
            AnchorNode anchorNode1 = new AnchorNode(hitResult.createAnchor());
            anchorNode1.setParent(arFragment.getArSceneView().getScene());
            MaterialFactory.makeOpaqueWithColor(HelloSceneformActivity.this,new Color(0.33f, 0.87f, 0f))
                    .thenAccept(new Consumer<Material>() {
                        @Override
                        public void accept(Material material) {
                            ModelRenderable modelRenderable = ShapeFactory.makeSphere(0.02f, Vector3.zero(), material);
                            anchorNode1.setRenderable(modelRenderable);
                            sphereNodeArray.add(anchorNode1);
                        }
                    });
        }
    }

    private void drawLine(Anchor firstAnchor, Anchor secondAnchor, Double length) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            AnchorNode firstAnchorNode = new AnchorNode(firstAnchor);
            startNodeArray.add(firstAnchorNode);

            AnchorNode secondAnchorNode = new AnchorNode(secondAnchor);
            endNodeArray.add(secondAnchorNode);

            firstAnchorNode.setParent(arFragment.getArSceneView().getScene());
            secondAnchorNode.setParent(arFragment.getArSceneView().getScene());

            MaterialFactory.makeOpaqueWithColor(HelloSceneformActivity.this, new Color(0.53f, 0.92f, 0f)).thenAccept(new Consumer<Material>() {
                @Override
                public void accept(Material material) {
                    ModelRenderable modelRenderable = ShapeFactory.makeSphere(0.02f, new Vector3(0.0f, 0.0f, 0.0f), material);
                    Node node = new Node();
                    node.setParent(secondAnchorNode);
                    node.setLocalPosition(Vector3.zero());
                    node.setRenderable(modelRenderable);
                    sphereNodeArray.add(node);
                }
            });

            Vector3 firstWorldPosition = firstAnchorNode.getWorldPosition();
            Vector3 secondWorldPosition = secondAnchorNode.getWorldPosition();

            Vector3 difference = Vector3.subtract(firstWorldPosition, secondWorldPosition);
            Vector3 directionFromTopToBottom = difference.normalized();
            Quaternion rotationFromAToB = Quaternion.lookRotation(directionFromTopToBottom, Vector3.up());

            MaterialFactory.makeOpaqueWithColor(HelloSceneformActivity.this, new Color(0.53f, 0.92f, 0f)).thenAccept(new Consumer<Material>() {
                @Override
                public void accept(Material material) {

                    ModelRenderable modelRenderable = ShapeFactory.makeCube(new Vector3(0.01f, 0.01f, difference.length()), Vector3.zero(), material);

                    Node lineNode = new Node();

                    lineNode.setParent(firstAnchorNode);
                    lineNode.setRenderable(modelRenderable);
                    lineNode.setWorldPosition(Vector3.add(firstWorldPosition, secondWorldPosition).scaled(0.5f));
                    lineNode.setWorldRotation(rotationFromAToB);

                    lineNodeArray.add(lineNode);

                    ViewRenderable.builder().setView(HelloSceneformActivity.this, R.layout.renderable_text)
                            .build()
                            .thenAccept(new Consumer<ViewRenderable>() {
                                @Override
                                public void accept(ViewRenderable viewRenderable) {
                                    TextView view = (TextView) viewRenderable.getView();
                                    view.setText(new DecimalFormat(".00").format(length * 100)+ "CM");
                                    viewRenderable.setShadowCaster(false);
                                    FaceToCameraNode faceToCameraNode = new FaceToCameraNode();
                                    faceToCameraNode.setParent(firstAnchorNode);
                                    faceToCameraNode.setWorldRotation(Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 90f));
                                    faceToCameraNode.setWorldPosition(new Vector3(0f, 0.02f, 0f));
                                    faceToCameraNode.setRenderable(viewRenderable);
                                }
                            });
                }
            });


        }
    }


    static class AnchorInfoBean{

      public AnchorInfoBean(String dataText, Anchor anchor, Double length) {
          this.dataText = dataText;
          this.anchor = anchor;
          this.length = length;
      }

      public String dataText;
    public Anchor anchor;
    public Double length;
  }


    private void showCustom3DModle(HitResult hitResult) {
        Anchor anchor = hitResult.createAnchor();

        Log.e(TAG, "plane: "+ anchor.getPose());
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());
        // Create the transformable andy and add it to the anchor.
        andy = new TransformableNode(arFragment.getTransformationSystem());
        andy.setParent(anchorNode);
        andy.setRenderable(andyRenderable);
        andy.setWorldRotation(Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 90f));
        Toast.makeText(HelloSceneformActivity.this,"Z：：："+andy.getWorldPosition().z+"：：：X::::"+andy.getWorldPosition().x+"::YYY::"+andy.getWorldPosition().y,Toast.LENGTH_LONG).show();
        andy.select();
    }


    TransformableNode andy;

    float radiation=90f;
    public void loadModel(View view) {

        Session session = arFragment.getArSceneView().getSession();
        float[] pos = { 0, 0, -1 };
        float[] rotation = { 0, 0, 0, 1 };
        Anchor anchor =  session.createAnchor(new Pose(pos, rotation));
        AnchorNode  anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());
        andy = new TransformableNode(arFragment.getTransformationSystem());
        andy.setParent(anchorNode);
        andy.setRenderable(andyRenderable);
        andy.setWorldRotation(Quaternion.axisAngle(new Vector3(0f, 1f, 0f), radiation));
        Toast.makeText(HelloSceneformActivity.this,"Z：：："+andy.getWorldPosition().z+"：：：X::::"+andy.getWorldPosition().x+"::YYY::"+andy.getWorldPosition().y,Toast.LENGTH_LONG).show();
        andy.select();
    }


    public void zoom_in(View view) {
        modleMovement(andy,"zoom_in");
    }
    Vector3 localPosition;
    private void modleMovement(TransformableNode node, String moveMent) {
        Vector3 currentPosition = new Vector3();
        Vector3 move = new Vector3();

        try {
            currentPosition = Objects.requireNonNull(node.getLocalPosition());

            if (moveMent.equals("down")) {
                move.set(currentPosition.x, (float) (currentPosition.y - 0.1), currentPosition.z);
                localPosition = move;
            }

            if (moveMent.equals("up"))
            if (moveMent.equals("right_move")) {

                move.set((float) (currentPosition.x + 0.1), currentPosition.y, currentPosition.z);
                localPosition = move;
            }
            if (moveMent.equals("left_move")) {

                move.set((float) (currentPosition.x - 0.1), currentPosition.y, currentPosition.z);
                localPosition = move;
            }
            if (moveMent.equals("rotate_left")) {
                localPosition = currentPosition;
            }

            if (moveMent.equals("rotate_right")) {
                localPosition = currentPosition;
//                rotateLeft(node,localPosition);
            }

            if (moveMent.equals("zoom_in")) {
                move.set(currentPosition.x, currentPosition.y, (float) (currentPosition.z + 0.1));
                localPosition = move;
            }

            if (moveMent.equals("zoom_out")) {
                move.set(currentPosition.x, currentPosition.y, (float) (currentPosition.z - 0.1));
                localPosition = move;
            }


            node.setLocalPosition(move);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private ObjectAnimator createAnimator() {

        // 节点的位置和角度信息设置通过Quaternion来设置
        // 创建4个Quaternion 来设置四个关键位置
        Quaternion orientation1 = Quaternion.axisAngle(new Vector3(0f,1f,0f), 0f);
        Quaternion orientation2 = Quaternion.axisAngle(new Vector3(0f,1f,0f), 120f);
        Quaternion orientation3 = Quaternion.axisAngle(new Vector3(0f,1f,0f), 240f);
        Quaternion orientation4 = Quaternion.axisAngle(new Vector3(0f,1f,0f), 360f);
        ObjectAnimator rotationAnimation = new ObjectAnimator();
        rotationAnimation.setObjectValues(orientation1, orientation2, orientation3, orientation4);
        // 设置属性动画修改的属性为 localRotation
        rotationAnimation.setPropertyName("localRotation");
        // 使用Sceneform 框架提供的估值器 QuaternionEvaluator 作为属性动画估值器
        rotationAnimation.setEvaluator(new QuaternionEvaluator());
        //  设置动画重复无限次播放。
        rotationAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        rotationAnimation.setRepeatMode(ObjectAnimator.RESTART);
        rotationAnimation.setInterpolator(new LinearInterpolator());
        rotationAnimation.setAutoCancel(true);
        return rotationAnimation;
    }
    ObjectAnimator leftRotationAnimation;


    public void zoom_out(View view) {
        modleMovement(andy,"zoom_out");
    }
    private ModelAnimator animator;
    // Index of the current animation playing.
    public void rotate_left(View view) {
//        rotateLeft(andy);
        if (andy!=null)
                andy.setWorldRotation(Quaternion.axisAngle(new Vector3(0f, 1f, 0f), radiation+=30));
    }

    public void rotate_right(View view) {
        if (andy!=null)
                andy.setWorldRotation(Quaternion.axisAngle(new Vector3(0f, 1f, 0f), radiation-=30));
//       rotateRight(andy);
    }
    private void rotateLeft(TransformableNode node) {
        if (leftRotationAnimation==null){
            leftRotationAnimation= createAnimator();
            leftRotationAnimation.setTarget(node);
            leftRotationAnimation.setDuration(5000L);
        }
        leftRotationAnimation.start();
    }
    private void rotateRight(TransformableNode andy) {
        leftRotationAnimation.reverse();
    }
    public void down(View view) {
        modleMovement(andy,"down");
    }
    public void up(View view) {
        modleMovement(andy,"up");
    }

    public void left(View view) {
        modleMovement(andy,"left_move");
    }

    public void right(View view) {
        modleMovement(andy,"right_move");
    }

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * <p>Finishes the activity if Sceneform can not run
     */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }
}
