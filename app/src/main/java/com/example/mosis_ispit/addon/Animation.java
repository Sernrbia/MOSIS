package com.example.mosis_ispit.addon;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;

public class Animation {

    private AnimationDrawable animation;
    private ImageView imageView;

    public Animation(ImageView imageView) {
        this.imageView = imageView;
        this.animation = (AnimationDrawable) imageView.getDrawable();
    }

    public void startAnimation() {
        this.imageView.setVisibility(View.VISIBLE);
        this.animation.start();
    }

    public void stopAnimation() {
        this.imageView.setVisibility(View.INVISIBLE);
        this.animation.stop();
    }

}
