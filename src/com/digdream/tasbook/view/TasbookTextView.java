package com.digdream.tasbook.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


public class TasbookTextView extends TextView
{
  public TasbookTextView(Context paramContext)
  {
    this(paramContext, null);
  }

  public TasbookTextView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }

  public TasbookTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    if (!isInEditMode())
      setTypeface(Typeface.createFromAsset(paramContext.getAssets(), "fonts/Roboto/Roboto-Light.ttf"));
  }

  public void setText(String paramString)
  {
    if (paramString == null);
    for (String str = ""; ; )
    {
      super.setText(str);
      return;
    }
  }
}