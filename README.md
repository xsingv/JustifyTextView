JustifyTextView
===============
  实现方式本身有缺陷，可能有bug，不要用于生产环境。有兴趣的,可以参考学习下...<br/>
  两端分散对齐的TextView,支持中英文混编

######设置行距的属性;其他属性直接使用原TextView的属性,在Activity中使用也是直接使用TextView
```xml
    xmlns:custom="http://schemas.android.com/apk/res/com.xs.justifytextview"
    custom:lineSpacing="10sp"
```
```java
    TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.JustifyTextView, defStyle, 0);
		int n = a.getIndexCount();
		for (int i = 0; i < n; i++)
		{
			int attr = a.getIndex(i);
			switch (attr)
			{
			case R.styleable.JustifyTextView_lineSpacing:
				lineSpacing = a.getDimensionPixelSize(attr, (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_SP, 3,
								getResources().getDisplayMetrics()));
				break;
			}

		}
		a.recycle();
```
###效果图:blush::blush::blush:
![运行效果](/images/justifytextView.png)  
