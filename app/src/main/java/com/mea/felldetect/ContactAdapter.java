package com.mea.felldetect;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Muneef on 27/04/15.
 */
public class ContactAdapter extends BaseAdapter {

    List<Contacts> citems;
    Context context;
    TextView tvNamec;
    TextView number;
    CardView cv;
    ImageView delete;

    public ContactAdapter(Context c,List<Contacts> list){

        this.citems = list;
        this.context = c;

    }


    @Override
    public int getCount() {
        return citems.size();
    }

    @Override
    public Object getItem(int i) {
        return citems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.conatct_items, viewGroup, false);
        final DbHelper db = new DbHelper(context);
        TextView num = (TextView) v.findViewById(R.id.num);
        TextView namec = (TextView) v.findViewById(R.id.idname);
        delete = (ImageView) v.findViewById(R.id.deleteImg);

        num.setText(citems.get(i)._phone_number);
        namec.setText(citems.get(i)._name);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteContact(citems.get(i));
                citems = db.getAllContacts();
                Toast.makeText(context, "Contact Deleted", Toast.LENGTH_SHORT).show();

                notifyDataSetChanged();

            }
        });

      /*  cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteContact(citems.get(i));

                //notifyDataSetChanged();
                Toast.makeText(context,"long press",Toast.LENGTH_SHORT);

            }
        });
*/

      /*  cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
            db.deleteContact(citems.get(i));

            notifyDataSetChanged();
                Toast.makeText(context,"long press",Toast.LENGTH_SHORT);
                return true;
            }
        });
*/

        return v;
    }
}
