package com.application.testfirebase1;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	// * Local Variables :
	private final List<Note> mList = new ArrayList<> ();
	private final FirebaseDatabase database = FirebaseDatabase.getInstance ();
	private View dialogView = null;
	private RecyclerView recyclerView;
	private DatabaseReference reference;
	private AlertDialog.Builder alert;
	private AlertDialog dialog;

	// * Override OnCreate :
	@SuppressLint ("InflateParams")
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		// ? Create MainActivity View :
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);

		// ? Show the Registration/Login Dialog :
		alert = new AlertDialog.Builder (MainActivity.this);
		dialogView = getLayoutInflater ().inflate (R.layout.popup_register_example, null);
		dialog = alert.setView (dialogView).setCancelable (false).create ();
		dialog.getWindow ().setBackgroundDrawable (new ColorDrawable (Color.TRANSPARENT));
		dialog.show ();

		// ? Set onClickListener for [Register/AlertDialog] -> [Continue/Button] :
		dialogView.findViewById (R.id.Popup_Register_Continue_Button).setOnClickListener (view -> {
			TextView username = dialogView.findViewById (R.id.Popup_Register_Username),
					password_TV = dialogView.findViewById (R.id.Popup_Register_Password);
			RadioGroup register_RadioGroup = dialogView.findViewById (R.id.Popup_Register_RadioGroup);
			Toast.makeText (MainActivity.this, register_RadioGroup.getCheckedRadioButtonId () + "", Toast.LENGTH_SHORT).show ();
			dialog.dismiss ();
		});

		// ? Initialize the root of Firebase Database :
		reference = database.getReference ("Notes");

		// ? Set onClickListener for [floating/Button] :
		findViewById (R.id.X_floatingActionButton).setOnClickListener (view -> {
			// ? Create and Show [Add new note/AlertDialog] :
			alert = new AlertDialog.Builder (MainActivity.this);
			dialogView = getLayoutInflater ().inflate (R.layout.popup_new_example, null);
			dialog = alert.setView (dialogView).create ();
			dialog.getWindow ().setBackgroundDrawable (new ColorDrawable (Color.TRANSPARENT));
			dialog.show ();

			// ? Set onClickListener for [floating/Button] -> [Send to Firebase/Button] :
			dialogView.findViewById (R.id.Popup_New_Send_Button).setOnClickListener (view1 -> {
				TextView title = dialogView.findViewById (R.id.Popup_New_Title),
						text = dialogView.findViewById (R.id.Popup_New_Text);
				// ! add [new note/Note] to Firebase Database just if fields are not empty :
				if (!title.getText ().toString ().isEmpty () && !text.getText ().toString ().isEmpty ()) {
					String id = reference.push ().getKey ();
					Note note = new Note (id, title.getText ().toString (), text.getText ().toString ());
					reference.child (id).setValue (note);
					// ? Hide [Add new note/AlertDialog] :
					dialog.dismiss ();
				} else
					Toast.makeText (MainActivity.this, "setOnClickListener ERROR", Toast.LENGTH_SHORT).show ();
			});
		});

/*
		FirebaseDatabase database = FirebaseDatabase.getInstance ();
		DatabaseReference ref = database.getReference ("Name");
		ref.setValue (value_ET.getText ().toString ());
		ref.addValueEventListener (new ValueEventListener () {
			@Override
			public void onDataChange (@NonNull DataSnapshot snapshot) {
				String dataValue = snapshot.getValue (String.class);
				name_TV.setText (dataValue);
			}

			@Override
			public void onCancelled (@NonNull DatabaseError error) {
				Toast.makeText (MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show ();
			}
		});
*/
	}

	// * Override OnStart :
	@Override
	protected void onStart () {
		super.onStart ();
		// ? Set addValueEventListener for [reference/DatabaseReference] :
		reference.addValueEventListener (new ValueEventListener () {
			@Override
			public void onDataChange (@NonNull DataSnapshot snapshot) {
				mList.clear ();
				for (DataSnapshot data : snapshot.getChildren ())
					mList.add (0, data.getValue (Note.class));
				recyclerView = findViewById (R.id.X_recylerView);
				CustomListAdapter adapter = new CustomListAdapter (MainActivity.this, mList);
				adapter.setReferenceString ("Notes");
				recyclerView.setAdapter (adapter);
				recyclerView.setLayoutManager (new LinearLayoutManager (getApplicationContext ()));
			}

			@Override
			public void onCancelled (@NonNull DatabaseError error) {
				Toast.makeText (MainActivity.this, "addValueEventListener ERROR", Toast.LENGTH_SHORT).show ();
			}
		});
	}
}