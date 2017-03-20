package net.ciapps.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;

/**
 * Created by Evgeny Cherkasov on 19.03.2017.
 */

public class RatingView extends RelativeLayout {
    private static final float DEFAULT_ENTRY_WIDTH = 24f;

    private LinearLayout layoutEntries;
    private ImageView imageSelector;
    private OnClickListener entryOnClickListener;

    private CharSequence[] entries;
    private int entryWidth;
    private int selectedPosition;
    private int selectedTextColor;
    private int textColor;

    public RatingView(Context context) {
        this(context, null);
    }

    public RatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    public RatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatingView, 0, 0);

        // Init attributes
        entries = typedArray.getTextArray(R.styleable.RatingView_android_entries);
        entryWidth = typedArray.getDimensionPixelSize(R.styleable.RatingView_entryWidth, convertDpToPixels(DEFAULT_ENTRY_WIDTH, context));
        selectedPosition = typedArray.getInteger(R.styleable.RatingView_selectedPosition, -1);
        selectedTextColor = typedArray.getColor(R.styleable.RatingView_selectedTextColor, ContextCompat.getColor(context, getThemeAttributeValue(context, R.attr.colorControlNormal)));
        textColor = typedArray.getColor(R.styleable.RatingView_android_textColor, ContextCompat.getColor(context, getThemeAttributeValue(context, R.attr.colorControlNormal)));
        typedArray.recycle();

        // Prepare layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.view_rating, this, true);
        layoutEntries = (LinearLayout) rootView.findViewById(R.id.layoutEntries);
        imageSelector = (ImageView) rootView.findViewById(R.id.imageSelector);
        entryOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                int unselectedPosition = selectedPosition;
                selectedPosition = (Integer) v.getTag();
                updateSelector(unselectedPosition);
            }
        };
        if (entries != null) {
            addEntryViews(context);
        }
        updateSelector(-1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (imageSelector.getMeasuredWidth() != 0) {
            int selectorWidth = imageSelector.getMeasuredWidth() - selectedPosition * entryWidth;
            int padding = (selectorWidth - entryWidth) / 2;
            layoutEntries.setPadding(padding, 0, padding, 0);
        }
    }

    private void addEntryViews(Context context) {
        layoutEntries.removeAllViews();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(entryWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        for (int i = 0; i < entries.length; i++) {
            Button button = new Button(context);
            button.setText(entries[i]);
            button.setTag(i);
            button.setPadding(0, 0, 0, 0);
            button.setBackgroundResource(getThemeAttributeValue(context, R.attr.selectableItemBackground));
            button.setTextColor(textColor);
            button.setOnClickListener(entryOnClickListener);
            layoutEntries.addView(button, layoutParams);
        }

    }

    private void updateSelector(int unselectedPosition) {
        if (unselectedPosition != -1) {
            Button unselectedButton = (Button) layoutEntries.getChildAt(unselectedPosition);
            unselectedButton.setTextColor(textColor);
        }
        if (selectedPosition == -1) {
            imageSelector.setVisibility(GONE);
        }
        else {
            Button selectedButton = (Button) layoutEntries.getChildAt(selectedPosition);
            selectedButton.setTextColor(selectedTextColor);
            int offsetLeft = selectedPosition * entryWidth;
            if (imageSelector.getVisibility() == GONE) {
                imageSelector.setPadding(offsetLeft, 0, 0, 0);
                // TODO: Animate alpha to appear selector
                imageSelector.setVisibility(VISIBLE);
            }
            else {
                // TODO: Animate padding to make it moving from old position to new one
                imageSelector.setPadding(offsetLeft, 0, 0, 0);
            }
        }
    }

    private int convertDpToPixels(float dp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return px;
    }

    private int getThemeAttributeValue(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.resourceId;
    }

}
