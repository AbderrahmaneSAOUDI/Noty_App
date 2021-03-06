package com.application.testfirebase1;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
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
	FloatingActionButton add_FloatingButton, lougout_FloatingButton;
	private RecyclerView recyclerView;
	private DatabaseReference reference;
	private AlertDialog.Builder alert;
	private AlertDialog new_dialog, register_dialog;
	private View new_dialogView, register_dialogView;

	// * Override OnCreate :
	@SuppressLint ({"InflateParams", "NonConstantResourceId"})
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		// ? Create MainActivity View :
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);

		// ? Initialize Variables :
		reference = database.getReference ("Notes");
		alert = new AlertDialog.Builder (MainActivity.this);
		recyclerView = findViewById (R.id.X_recylerView);
		add_FloatingButton = findViewById (R.id.New_floatingABtn);
		lougout_FloatingButton = findViewById (R.id.Logout_floatingABtn);

		// ? Show the Registration/Login Dialog :
		register_dialogView = getLayoutInflater ().inflate (R.layout.popup_register_example, null);
		register_dialog = alert.setView (register_dialogView).setCancelable (false).create ();
		register_dialog.getWindow ().setBackgroundDrawable (new ColorDrawable (Color.TRANSPARENT));

		recyclerView.setVisibility (View.GONE);
		add_FloatingButton.setVisibility (View.GONE);
		lougout_FloatingButton.setVisibility (View.GONE);
		register_dialog.show ();

		// ? Set onClickListener for [Register-Login/AlertDialog] -> [Continue/Button] :
		register_dialogView.findViewById (R.id.Popup_Register_Continue_Button).setOnClickListener (view -> {
			TextView email_TV = register_dialogView.findViewById (R.id.Popup_Register_Email),
					password_TV = register_dialogView.findViewById (R.id.Popup_Register_Password);
			RadioGroup register_RadioGroup = register_dialogView.findViewById (R.id.Popup_Register_RadioGroup);
			FirebaseAuth auth = FirebaseAuth.getInstance ();

			// ? Check for empty fields and chosen RadioButton :
			if (email_TV.getText ().toString ().isEmpty ())
				Toast.makeText (this, "Please enter your Email address.", Toast.LENGTH_SHORT).show ();
			else if (password_TV.getText ().toString ().length () < 8)
				Toast.makeText (this, "Password must be at least 8 characters.", Toast.LENGTH_SHORT).show ();
			else
				switch (register_RadioGroup.getCheckedRadioButtonId ()) {
					// ! <Register/RadioButton> : create new account and return "REGISTER Success" if done, "ERROR" else then hide [Register-Login/AlertDialog] :
					case R.id.Popup_Register_RadioGroup_Register:
						auth.createUserWithEmailAndPassword (email_TV.getText ().toString (), password_TV.getText ().toString ()).addOnCompleteListener (task -> {
							if (task.isSuccessful ()) {
								auth.getCurrentUser ().sendEmailVerification ().addOnCompleteListener (task1 -> {
									if (task1.isSuccessful ())
										Toast.makeText (this, "Verification link sent to your Email. Please send it before loggig in", Toast.LENGTH_SHORT).show ();
								});
								if (auth.getCurrentUser ().isEmailVerified ()) {
									recyclerView.setVisibility (View.VISIBLE);
									add_FloatingButton.setVisibility (View.VISIBLE);
									lougout_FloatingButton.setVisibility (View.VISIBLE);
									register_dialog.dismiss ();
								}
							} else
								Log.e ("ERROR", task.getException ().toString ());
						});
						break;
					// ! <Login/RadioButton> : login to an existing account and return "LOGIN Success" if done, "ERROR" else then hide [Register-Login/AlertDialog] :
					case R.id.Popup_Register_RadioGroup_Login:
						auth.signInWithEmailAndPassword (email_TV.getText ().toString (), password_TV.getText ().toString ()).addOnCompleteListener (task -> {
							if (auth.getCurrentUser ().isEmailVerified ())
								if (task.isSuccessful ()) {
									recyclerView.setVisibility (View.VISIBLE);
									add_FloatingButton.setVisibility (View.VISIBLE);
									lougout_FloatingButton.setVisibility (View.VISIBLE);
									register_dialog.dismiss ();
								} else
									Log.e ("ERROR", task.getException ().toString ());
							else
								Toast.makeText (this, "Please Verify your Email Address.", Toast.LENGTH_SHORT).show ();
						});
						break;
					// ! <NULL/RadioButton> : Show Toast error message :
					default:
						Toast.makeText (this, "Please choose [Register / Login]", Toast.LENGTH_SHORT).show ();
						break;
				}
		});

		// ? Set onClickListener for [New Note/FloatingActionButton] :
		add_FloatingButton.setOnClickListener (view -> {
			// ? Create and Show [Add new note/AlertDialog] :
			new_dialogView = getLayoutInflater ().inflate (R.layout.popup_new_example, null);
			new_dialog = alert.setView (new_dialogView).create ();
			new_dialog.getWindow ().setBackgroundDrawable (new ColorDrawable (Color.TRANSPARENT));
			new_dialog.setCancelable (true);
			new_dialog.show ();

			// ? Set onClickListener for [Add new note/AlertDialog] -> [Add new note/Button] :
			new_dialogView.findViewById (R.id.Popup_New_Send_Button).setOnClickListener (view1 -> {
				TextView title = new_dialogView.findViewById (R.id.Popup_New_Title),
						text = new_dialogView.findViewById (R.id.Popup_New_Text);
				// ! add [new note/Note] to Firebase Database just if fields are not empty :
				if (! title.getText ().toString ().isEmpty () && ! text.getText ().toString ().isEmpty ()) {
					String id = reference.push ().getKey ();
					Note note = new Note (id, FirebaseAuth.getInstance ().getCurrentUser ().getUid (), title.getText ().toString (), text.getText ().toString ());
					reference.child (id).setValue (note);
					// ? Hide [Add new note/AlertDialog] :
					new_dialog.dismiss ();
				} else if (title.getText ().toString ().isEmpty ()) {
					String id = reference.push ().getKey ();
					Note note = new Note (id, FirebaseAuth.getInstance ().getCurrentUser ().getUid (), "<No Title>", text.getText ().toString ());
					reference.child (id).setValue (note);
					// ? Hide [Add new note/AlertDialog] :
					new_dialog.dismiss ();
					Toast.makeText (this, "Title is set by default to \"<No Title>\"", Toast.LENGTH_SHORT).show ();
				} else
					Log.e ("ERROR", "setOnClickListener ERROR");
			});

			// ? Set onClickListener for [Add new note/AlertDialog] -> [Cancel/Button] :
			new_dialogView.findViewById (R.id.Popup_New_Cancel_Button).setOnClickListener (view1 -> new_dialog.dismiss ());
		});

		// ? Set onClickListener for [Logout/FloatingActionButton] :
		lougout_FloatingButton.setOnClickListener (view -> {
			recyclerView.setVisibility (View.GONE);
			add_FloatingButton.setVisibility (View.GONE);
			lougout_FloatingButton.setVisibility (View.GONE);
			register_dialog.show ();
		});
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
					if (data.getValue (Note.class).getUserID ().equals (FirebaseAuth.getInstance ().getUid ()))
						mList.add (0, data.getValue (Note.class));
				CustomListAdapter adapter = new CustomListAdapter (MainActivity.this, mList);
				adapter.setReferenceString ("Notes");
				recyclerView.setAdapter (adapter);
				recyclerView.setLayoutManager (new LinearLayoutManager (getApplicationContext ()));
			}

			@Override
			public void onCancelled (@NonNull DatabaseError error) {
				Log.e ("ERROR", "addValueEventListener ERROR");
			}
		});

		// ! disable [login-register dialog/AlertDialog] if user already registered or logged in :
		FirebaseAuth auth = FirebaseAuth.getInstance ();
		if (auth.getCurrentUser () == null || ! auth.getCurrentUser ().isEmailVerified ()) {
			recyclerView.setVisibility (View.GONE);
			add_FloatingButton.setVisibility (View.GONE);
			lougout_FloatingButton.setVisibility (View.GONE);
			register_dialog.show ();

		} else {
			recyclerView.setVisibility (View.VISIBLE);
			add_FloatingButton.setVisibility (View.VISIBLE);
			lougout_FloatingButton.setVisibility (View.VISIBLE);
			register_dialog.dismiss ();
		}
	}

//	protected static void set
}