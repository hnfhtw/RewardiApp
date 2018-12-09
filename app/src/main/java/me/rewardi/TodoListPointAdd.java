package me.rewardi;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TodoListPointAdd extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextRewardi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolistpoint_add);

        editTextName = findViewById(R.id.editTextName);
        editTextRewardi = findViewById(R.id.editTextRewardi);

        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isValid()) {
                            Intent intent = new Intent();
                            intent.putExtra("name", editTextName.getText().toString());
                            intent.putExtra("rewardi", editTextRewardi.getText().toString());
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            showAlertDialog();
                        }
                    }
                });
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
        if(isEditTextInteger(editTextRewardi))
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
}
