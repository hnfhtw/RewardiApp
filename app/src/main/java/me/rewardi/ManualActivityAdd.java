package me.rewardi;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.parceler.Parcels;

public class ManualActivityAdd extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextRewardiPerHour;
    private ManualActivity act;
    private Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_add);

        editTextName = findViewById(R.id.editTextName);
        editTextRewardiPerHour = findViewById(R.id.editTextRewardiPerHour);

        buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isValid()) {
                            Intent intent = new Intent();
                            if(hasExtras()){    // edit existing manual activity
                                act.setName(editTextName.getText().toString());
                                act.setRewardiPerHour(Integer.parseInt(editTextRewardiPerHour.getText().toString()));
                            }
                            else{   // create new manual activity
                                act = new ManualActivity(0, editTextName.getText().toString(), Integer.parseInt(editTextRewardiPerHour.getText().toString()), false, null);
                            }

                            Bundle bundle = new Bundle();
                            bundle.putParcelable("act", Parcels.wrap(act));
                            intent.putExtras(bundle);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            showAlertDialog();
                        }
                    }
                });

        checkExtras();
    }

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
        if(isEditTextEmpty(editTextName))
            return false;
        if(isEditTextInteger(editTextRewardiPerHour))
            return false;
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
            act = Parcels.unwrap(getIntent().getExtras().getParcelable("act"));
            editTextName.setText(act.getName());
            editTextRewardiPerHour.setText(Integer.toString(act.getRewardiPerHour()));
            setTitle("Edit Activity");
            buttonAdd.setText("Edit");
        }
        else{
            setTitle("Add new Activity");
            buttonAdd.setText("Add");
        }
    }

    private boolean hasExtras(){
        return getIntent().getExtras() != null;
    }
}
