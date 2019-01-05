package me.rewardi;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.parceler.Parcels;

public class GadgetAdd extends AppCompatActivity {

    private EditText editTextTrustNumber;
    private EditText editTextGadgetName;
    private EditText editTextRewardi;
    private EditText editTextMaxTime;
    private TextView textViewRewardi;
    private TextView textViewMaxTime;
    private TextView textViewGadgetName;

    private SocketBoard socketBoard;
    private Box box;
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
                            Bundle bundle = new Bundle();
                            if(gadgetType.equals("SocketBoard")){
                                if(hasExtras()) {
                                    socketBoard.setName(editTextGadgetName.getText().toString());
                                    socketBoard.setRewardiPerHour(Integer.parseInt(editTextRewardi.getText().toString()));
                                    socketBoard.setMaxTimeSec(Integer.parseInt(editTextMaxTime.getText().toString()));
                                    bundle.putParcelable("socketBoard", Parcels.wrap(socketBoard));
                                }
                                else{
                                    SocketBoard newSocketBoard = new SocketBoard(0, editTextTrustNumber.getText().toString(), editTextGadgetName.getText().toString(), Integer.parseInt(editTextRewardi.getText().toString()), Integer.parseInt(editTextMaxTime.getText().toString()), false);
                                    bundle.putParcelable("socketBoard", Parcels.wrap(newSocketBoard));
                                }

                            }
                            else if(gadgetType.equals("Box")){
                                if(hasExtras()) {
                                    box.setName(editTextGadgetName.getText().toString());
                                    box.setRewardiPerOpen(Integer.parseInt(editTextRewardi.getText().toString()));
                                    bundle.putParcelable("box", Parcels.wrap(box));
                                }
                                else{
                                    Box newBox = new Box(0, editTextTrustNumber.getText().toString(), editTextGadgetName.getText().toString(), Integer.parseInt(editTextRewardi.getText().toString()), false);
                                    bundle.putParcelable("box", Parcels.wrap(newBox));
                                }
                            }

                            intent.putExtras(bundle);
                            intent.putExtra("gadgetType", gadgetType);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            showAlertDialog();
                        }
                    }
                });

        checkExtras();
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
                    textViewRewardi.setText("Rewardi / Hour");
                    textViewGadgetName.setText("Socket Board Name");
                    textViewRewardi.setVisibility(View.VISIBLE);
                    editTextRewardi.setVisibility(View.VISIBLE);
                    textViewMaxTime.setVisibility(View.VISIBLE);
                    editTextMaxTime.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void afterTextChanged(Editable s) { }
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

    private void checkExtras(){
        if(hasExtras()){
            boolean isSocketBoard = false;
            if(getIntent().getExtras().containsKey("socketBoard")){
                Log.d("GadgetAdd", "SocketBoard!");
                socketBoard = (SocketBoard) Parcels.unwrap(getIntent().getExtras().getParcelable("socketBoard"));
                isSocketBoard = true;
                editTextGadgetName.setText(socketBoard.getName());
                editTextTrustNumber.setText(socketBoard.getTrustNumber());
                editTextRewardi.setText(Integer.toString(socketBoard.getRewardiPerHour()));
                editTextMaxTime.setText(Integer.toString(socketBoard.getMaxTimeSec()));
                return;
            }
            if(isSocketBoard == false){
                Log.d("GadgetAdd", "Box!");
                box = (Box) Parcels.unwrap(getIntent().getExtras().getParcelable("box"));
                editTextGadgetName.setText(box.getName());
                editTextTrustNumber.setText(box.getTrustNumber());
                editTextRewardi.setText(Integer.toString(box.getRewardiPerOpen()));
            }
        }
    }

    private boolean hasExtras(){
        return getIntent().getExtras() != null;
    }
}
