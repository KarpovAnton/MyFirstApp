package com.karpov.vacuum.views.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.karpov.vacuum.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestVH extends RecyclerView.ViewHolder {

    @BindView(R.id.textt)
    TextView text;

    public TestVH(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(String row) {
        text.setText(row);
    }
}
