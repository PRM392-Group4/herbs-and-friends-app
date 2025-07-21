package com.group4.herbs_and_friends_app.ui.customer_side.checkout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.databinding.DialogHEditAddressBinding;

public class HEditAddressDialog extends DialogFragment {

    private DialogHEditAddressBinding binding;
    private OnAddressUpdatedListener listener;

    // Callback interface to send data back to HCheckoutFragment
    public interface OnAddressUpdatedListener {
        void onAddressUpdated(String recipientName, String recipientPhone, String address);
    }

    // Allow setting initial values for the dialog
    private static final String ARG_RECIPIENT_NAME = "recipient_name";
    private static final String ARG_RECIPIENT_PHONE = "recipient_phone";
    private static final String ARG_ADDRESS = "address";

    public static HEditAddressDialog newInstance(String recipientName, String recipientPhone, String address) {
        HEditAddressDialog dialog = new HEditAddressDialog();
        Bundle args = new Bundle();
        args.putString(ARG_RECIPIENT_NAME, recipientName);
        args.putString(ARG_RECIPIENT_PHONE, recipientPhone);
        args.putString(ARG_ADDRESS, address);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set dialog style to full width
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogHEditAddressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Populate fields with existing data if provided
        Bundle args = getArguments();
        if (args != null) {
            binding.editReceiverName.setText(args.getString(ARG_RECIPIENT_NAME, ""));
            binding.editReceiverPhone.setText(args.getString(ARG_RECIPIENT_PHONE, ""));
            binding.editReceiverAddress.setText(args.getString(ARG_ADDRESS, ""));
        }

        // Set up button listeners
        binding.btnSaveAddress.setOnClickListener(v -> {
            String recipientName = binding.editReceiverName.getText().toString().trim();
            String recipientPhone = binding.editReceiverPhone.getText().toString().trim();
            String address = binding.editReceiverAddress.getText().toString().trim();

            // Basic validation
            if (recipientName.isEmpty() || recipientPhone.isEmpty() || address.isEmpty()) {
                binding.textError.setText("Please fill in all fields");
                binding.textError.setVisibility(View.VISIBLE);
                return;
            }

            // Pass data back to listener
            if (listener != null) {
                listener.onAddressUpdated(recipientName, recipientPhone, address);
            }
            dismiss();
        });

        binding.btnBack.setOnClickListener(v -> dismiss());
    }

    // Method to set the callback listener
    public void setOnAddressUpdatedListener(OnAddressUpdatedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}