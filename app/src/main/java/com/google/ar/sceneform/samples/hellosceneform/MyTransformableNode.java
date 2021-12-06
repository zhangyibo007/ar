package com.google.ar.sceneform.samples.hellosceneform;

import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;

import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.QuaternionEvaluator;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

public class MyTransformableNode extends TransformableNode {
    public MyTransformableNode(TransformationSystem transformationSystem) {
        super(transformationSystem);
    }

    public ObjectAnimator objectAnimator;
    public long animationDuration = 5000L;

    public void startAnimation(){
        if (objectAnimator != null) {
            return;
        }
        objectAnimator.setTarget(this);
        objectAnimator = createAnimator();
        objectAnimator.start();

    }

    private ObjectAnimator createAnimator() {

        // 节点的位置和角度信息设置通过Quaternion来设置
        // 创建4个Quaternion 来设置四个关键位置
        Quaternion orientation1 = Quaternion.axisAngle(new Vector3(0f,-1f,0f), 0f);
        Quaternion orientation2 = Quaternion.axisAngle(new Vector3(0f,-1f,0f), 120f);
        Quaternion orientation3 = Quaternion.axisAngle(new Vector3(0f,-1f,0f), 240f);
        Quaternion orientation4 = Quaternion.axisAngle(new Vector3(0f,-1f,0f), 360f);
        ObjectAnimator rotationAnimation = new ObjectAnimator();
        rotationAnimation.setObjectValues(orientation1, orientation2, orientation3, orientation4);
        // 设置属性动画修改的属性为 localRotation
        rotationAnimation.setPropertyName("localRotation");
        // 使用Sceneform 框架提供的估值器 QuaternionEvaluator 作为属性动画估值器
        rotationAnimation.setEvaluator(new QuaternionEvaluator());
        //  设置动画重复无限次播放。
//        rotationAnimation.repeatCount = ObjectAnimator.INFINITE
//        rotationAnimation.repeatMode = ObjectAnimator.RESTART
        rotationAnimation.setDuration(animationDuration);
        rotationAnimation.setInterpolator(new LinearInterpolator());
        rotationAnimation.setAutoCancel(true);
        return rotationAnimation;
    }
}
