package com.digdream.tasbook.view;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class TasbookButton extends Button
{
  public TasbookButton(Context paramContext)
  {
    this(paramContext, null);
  }

  public TasbookButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }

  public TasbookButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    if (!isInEditMode())
      setTypeface(Typeface.createFromAsset(paramContext.getAssets(), "fonts/Roboto/Roboto-Light.ttf"));
  }
}