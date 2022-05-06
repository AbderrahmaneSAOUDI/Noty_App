package com.application.testfirebase1;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.CustomViewHolder> {
	// * Local Variables :
	private final List<Note> list;
	protected Context context;
	private String referenceString;
	private final FirebaseDatabase database = FirebaseDatabase.getInstance ();


	// * Constructors :
	public CustomListAdapter (Context _context, List<Note> _list) {
		this.context = _context;
		this.list = _list;
	}


	// * Getters :
	public String getReferenceString () {
		return referenceString;
	}


	// * Setters :
	public void setReferenceString (String _reference) {
		this.referenceString = _reference;
	}

	// * Methods :
	@NonNull
	@Override
	public CustomViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
		return new CustomViewHolder (LayoutInflater.from (parent.getContext ()).inflate (R.layout.list_example, parent, false));
	}

	@Override
	public void onBindViewHolder (@NonNull CustomViewHolder holder, int position) {
		holder.title.setText (list.get (position).getTitle ());
		holder.text.setText (list.get (position).getText ());
		holder.date.setText (list.get (position).getDate ());

		holder.itemView.setOnClickListener (view -> holder.text.setVisibility (View.VISIBLE));

		holder.itemView.setOnLongClickListener (view -> {
			AlertDialog.Builder alert = new AlertDialog.Builder (context);
			View dialogView = View.inflate (context, R.layout.popup_edit_example, null);
			final AlertDialog dialog = alert.setView (dialogView).create ();
			dialog.getWindow ().setBackgroundDrawable (new ColorDrawable (Color.TRANSPARENT));
			dialog.show ();

			EditText title = dialogView.findViewById (R.id.Popup_Edit_Title_TV);
			EditText text = dialogView.findViewById (R.id.Popup_Edit_Text_TV);
			title.setText (holder.title.getText ());
			text.setText (holder.text.getText ());
			dialogView.findViewById (R.id.Popup_Edit_Edit_Button).setOnClickListener (view1 -> {
				database.getReference (referenceString).child (list.get (position).getId ()).setValue (new Note (list.get (position).getId (), title.getText ().toString (), text.getText ().toString ()));
				dialog.dismiss ();
			});
			dialogView.findViewById (R.id.Popup_Edit_Delete_Button).setOnClickListener (view1 -> {
				database.getReference (referenceString).child (list.get (position).getId ()).removeValue ();
				dialog.dismiss ();
			});

			return false;
		});
	}

	@Override
	public int getItemCount () {
		return list.size ();
	}

	// * Inner Classes :
	public static class CustomViewHolder extends RecyclerView.ViewHolder {
		TextView title, date, text;

		public CustomViewHolder (@NonNull View itemView) {
			super (itemView);
			title = itemView.findViewById (R.id.List_Title);
			text = itemView.findViewById (R.id.List_Text);
			date = itemView.findViewById (R.id.List_Date);
		}
	}
}
