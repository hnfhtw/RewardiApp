package me.rewardi;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GadgetAdd extends AppCompatActivity {

    private EditText editTextTrustNumber;
    private EditText editTextGadgetName;
    private EditText editTextRewardi;
    private EditText editTextMaxTime;
    private TextView textViewRewardi;
    private TextView textViewMaxTime;
    private TextView textViewGadgetName;

    private String gadgetType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gadget_add);

        editTextTrustNumber = findViewById(R.id.editTextTrustNumber);
        editTextGadgetName = findViewById(R.id.editTextGadgetName);
        editTextRewardi = findViewById(R.id.editTextRewardi);
        editTextMaxTime = findViewById(R.id.editTextMaxTime);
        textViewRewardi = findViewById(R.id.textViewRewardi);
        textViewMaxTime = findViewById(R.id.textViewMaxTime);
        textViewGadgetName = findViewById(R.id.textViewGadgetName);
        editTextRewardi.setVisibility(View.INVISIBLE);
        editTextMaxTime.setVisibility(View.INVISIBLE);
        textViewRewardi.setVisibility(View.INVISIBLE);
        textViewMaxTime.setVisibility(View.INVISIBLE);

        editTextTrustNumber.addTextChangedListener(filterTextWatcher);

        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isValid()) {
                            Intent intent = new Intent();
                            intent.putExtra("trustNumber", editTextTrustNumber.getText().toString());
                            intent.putExtra("name", editTextGadgetName.getText().toString());
                            intent.putExtra("rewardi", editTextRewardi.getText().toString());
                            if(gadgetType.equals("SocketBoard")){
                                intent.putExtra("maxTimeSec", editTextMaxTime.getText().toString());
                            }
                            intent.putExtra("gadgetType", gadgetType);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            showAlertDialog();
                        }
                    }
                });
    }


    private TextWatcher filterTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(start == 0 && s.length() > 0){
                if(s.charAt(0) == '1'){        // trust number starting with '1' -> BOX
                    gadgetType = "Box";
                    textViewRewardi.setText("Rewardi per Open");
                    textViewGadgetName.setText("Box Name");
                    textViewRewardi.setVisibility(View.VISIBLE);
                    editTextRewardi.setVisibility(View.VISIBLE);
                    textViewMaxTime.setVisibility(View.INVISIBLE);
                    editTextMaxTime.setVisibility(View.INVISIBLE);
                }
                else if(s.charAt(0) == '2'){   // trust number starting with '2' -> SOCKETBOARD
                    gadgetType = "SocketBoard";
                    textViewRewardi.setText("Rewardi per Hour");
                    textViewGadgetName.setText("Socket Board Name");
                    textViewRewardi.setVisibility(View.VISIBLE);
                    editTextRewardi.setVisibility(View.VISIBLE);
                    textViewMaxTime.setVisibility(View.VISIBLE);
                    editTextMaxTime.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private void showAlertDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Make sure to fill all of the fields")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private boolean isValid(){
        if(isEditTextEmpty(editTextTrustNumber))
            return false;
        if(isEditTextEmpty(editTextGadgetName))
            return false;
        if(isEditTextInteger(editTextRewardi))
            return false;
        if(gadgetType.equals("SocketBoard")){
            if(isEditTextInteger((editTextMaxTime)))
                return false;
        }
        return true;
    }

    //helper method to check if editText is empty
    private boolean isEditTextEmpty(EditText editText){
        return editText.getText().toString().length() == 0;
    }

    //helper method to check if editText is an integer
    private boolean isEditTextInteger(EditText editText){
        try {
            int num = Integer.parseInt(editText.getText().toString());
        } catch (NumberFormatException e) {
            return false;
        }

        return editText.getText().toString().length() == 0;
    }
}
