// Generated by view binder compiler. Do not edit!
package com.example.myapplication.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.myapplication.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityPaymentHistoryBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button buttonBack;

  @NonNull
  public final RecyclerView paymentsRecyclerView;

  private ActivityPaymentHistoryBinding(@NonNull LinearLayout rootView, @NonNull Button buttonBack,
      @NonNull RecyclerView paymentsRecyclerView) {
    this.rootView = rootView;
    this.buttonBack = buttonBack;
    this.paymentsRecyclerView = paymentsRecyclerView;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityPaymentHistoryBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityPaymentHistoryBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_payment_history, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityPaymentHistoryBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonBack;
      Button buttonBack = ViewBindings.findChildViewById(rootView, id);
      if (buttonBack == null) {
        break missingId;
      }

      id = R.id.paymentsRecyclerView;
      RecyclerView paymentsRecyclerView = ViewBindings.findChildViewById(rootView, id);
      if (paymentsRecyclerView == null) {
        break missingId;
      }

      return new ActivityPaymentHistoryBinding((LinearLayout) rootView, buttonBack,
          paymentsRecyclerView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
