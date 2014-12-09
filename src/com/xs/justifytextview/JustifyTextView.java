package com.xs.justifytextview;

import java.lang.Character.UnicodeBlock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * 两端分散对齐的TextView,支持中英文混编</br> 你可以使用下面的自定义属性设置行距,其他属性和原生TextView一直并且有效</br>
 * xmlns:custom="http://schemas.android.com/apk/res/com.xs.justifytextview"<br>
 * custom:lineSpacing="10sp"<br>
 * 
 * @author xsing
 * 
 */
public class JustifyTextView extends TextView
{
	private static final String TAG = "JustifyTextView";
	/** 文本 */
	private String mText;

	/** 画笔 */
	private Paint mPaint;

	/** 文本宽度 */
	private int textWidth;

	/** 行距 */
	private float lineSpacing;

	public JustifyTextView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public JustifyTextView(Context context)
	{
		this(context, null);
	}

	public float mBaikeTextHeight = 0;// 文本高度
	public int mFontHeight = 0;

	public JustifyTextView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		/**
		 * 获得我们所定义的自定义样式属性,行距lineSpacing
		 */
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

		// TODO 获取原TextView的画笔,保持原属性不变
		mPaint = this.getPaint();
		// 获取文本颜色设置给画笔
		mPaint.setColor(this.getCurrentTextColor());
	}

	/** 单词单元数组,主要针对英文 */
	private String[] words;

	private void arrayTowords()
	{
		char[] array = mText.toCharArray();
		int j = 0;
		words = new String[array.length];
		for (int i = 0; i < array.length; i++)
		{
			words[i] = "";
			if(array[i] >= 0 && array[i] < 0x7f)
			{
				if(String.valueOf(array[i]).equals("\n"))
				{
					j++;
					words[j] = "\n";
					j++;
					continue;
				}
				words[j] = words[j] + (array[i] + "").trim();
				if(array.length - 1 > i + 1
						&& (array[i + 1] == ' ' || array[i + 1] == ' '))
				{
					j++;
				}

			} else
			{
				if(String.valueOf(array[i]).equals("\n"))
				{
					j++;
					words[j] = "\n";
					j++;
					continue;
				}
				words[j] = words[j] + (array[i] + "").trim();
				UnicodeBlock ub = Character.UnicodeBlock.of((array[i + 1]));
				if(ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
						|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
						|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
						|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
						|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS)
				{
					continue;
				}
				j++;
			}
		}

	}

	/**
	 * @return lines-int 重新排版后文档的行数
	 */
	private int getLines()
	{
		float linewidth = 0;
		int line = 0;
		float blankwidth = mPaint.measureText(" ");
		for (int i = 0; i < words.length; i++)
		{
			float measureText = mPaint.measureText(words[i]);

			if(linewidth + measureText >= textWidth)
			{
				if(words[i].isEmpty() || words[i] == "")
					break;
				line++;
				linewidth = 0;
				i--;
			} else
			{
				if(String.valueOf(words[i]).equals("\n"))
				{
					linewidth = textWidth;
				}
				if(mPaint.measureText(words[i]) != mPaint.measureText("中"))
				{
					linewidth += (measureText + blankwidth);
				} else
				{
					linewidth += measureText;
				}
			}
		}
		return line + 1;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		// super.onDraw(canvas);
		Log.d(TAG, "==============onDraw");
		float linewidth = 0;
		int point = 0;
		int line = 0;
		float blankwidth = mPaint.measureText(" ");
		for (int i = 0; i < words.length; i++)
		{
			float measureText = mPaint.measureText(words[i]);

			if(linewidth + measureText >= textWidth)
			{
				float widthPoint = 0;
				for (int k = point; k < i; k++)
				{

					if(String.valueOf(words[k]).equals("\n"))
					{

					} else
					{
						// TODO 核心,逐行逐个绘制单词word
						canvas.drawText(words[k],
								widthPoint + getPaddingLeft(),
								(float) (mPaint.getTextSize() + lineSpacing)
										* (line + 1) + getPaddingTop(), mPaint);
					}
					widthPoint = widthPoint + mPaint.measureText(words[k])
							+ ((textWidth - linewidth) / (i - point - 1));
					// 如果不是中文,增加一个空格
					if(mPaint.measureText(words[k]) != mPaint.measureText("过"))
					{
						widthPoint += blankwidth;
					}
				}
				// if(words[i]);
				line++;
				point = i;
				linewidth = 0;
				widthPoint = 0;
				i--;
			} else
			{ // 逐个单词累计,长度够一行绘制一次or换行
				if(String.valueOf(words[i]).equals("\n"))
				{
					linewidth = textWidth;
				}
				// 英文每个单词后面有一个空格
				if(mPaint.measureText(words[i]) != mPaint.measureText("中"))
				{
					linewidth += (measureText + blankwidth);
				} else
				{
					linewidth += measureText;
				}
			}
		}
		Log.d(TAG, "lines=====ondraw" + line);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		Log.d(TAG, "==============onMeasure");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 此处得到的是TextView的宽度;高度需重新计算
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);

		int width = widthSize;
		// 减去左右文本边距的文本区域宽度
		textWidth = widthSize - getPaddingLeft() - getPaddingRight();
		int height = 1000;// TextView高度

		// 获取text,分析构造单词数组,并计算出行数
		mText = (String) this.getText();
		arrayTowords();
		int lines = getLines();
		Log.d(TAG, "lines" + lines);

		float fontSpacing = mPaint.getFontSpacing();// 推荐行间距
		FontMetricsInt fontMetricsInt = mPaint.getFontMetricsInt();
		int fontheight = fontMetricsInt.bottom - fontMetricsInt.top;

		height = (int) (lines * (mPaint.getTextSize() + lineSpacing));// 0.8偏大
		Log.d(TAG,
				"width" + width + "  height:" + height + " fontheight:"
						+ fontheight + " textSize:" + mPaint.getTextSize()
						+ " fontSpacing:" + fontSpacing + "mPaint属性:"
						+ mPaint.getColor());
		setMeasuredDimension(widthSize, height + getPaddingBottom());
	}

}
