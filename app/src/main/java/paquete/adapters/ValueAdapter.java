package paquete.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.devspark.robototextview.widget.RobotoTextView;

import java.util.ArrayList;

import paquete.tufanoapp.R;

public class ValueAdapter extends BaseAdapter implements Filterable
{

    private final ArrayList<String> mStringFilterList;
    private final LayoutInflater    mInflater;
    private final ArrayList<String> mStringFilterList2;
    private       ArrayList<String> mStringList, mStringList2;
    private ArrayList<String> resultado;
    private ValueFilter       valueFilter;

    public ValueAdapter(ArrayList<String> mStringList, ArrayList<String> mStringList2, Context context)
    {

        this.mStringList = mStringList;
        this.mStringList2 = mStringList2;
        this.mStringFilterList = mStringList;
        this.mStringFilterList2 = mStringList2;
        mInflater = LayoutInflater.from(context);
        getFilter();
    }

    public int obtener_id_Cliente(int pos)
    {
        return Integer.parseInt(mStringList2.get(pos));
    }

    //How many items are in the data set represented by this Adapter.
    @Override
    public int getCount()
    {
        return mStringList.size();
    }

    //Get the data item associated with the specified position in the data set.
    @Override
    public Object getItem(int position)
    {
        return mStringList.get(position);
    }

    //Get the row id associated with the specified position in the list.
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    //Get a View that displays the data at the specified position in the data set.
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //Holder viewHolder;

        RobotoTextView title;

        if (convertView == null)
        {
            //viewHolder = new Holder();
            convertView = mInflater.inflate(R.layout.list_item, parent, false);
            //viewHolder.nameTv = (TextView) convertView.findViewById(R.id.txt_listitem);
            title = (RobotoTextView) convertView.findViewById(R.id.txt_listitem);

            convertView.setTag(title);
        }
        else
        {
            title = (RobotoTextView) convertView.getTag();
            //viewHolder = (Holder) convertView.getTag();
        }

        title.setText(mStringList.get(position));

        return convertView;
    }

    /*private class Holder
    {
        TextView nameTv;
    }*/

    //Returns a filter that can be used to constrain data with a filtering pattern.
    @Override
    public Filter getFilter()
    {
        if (valueFilter == null)
        {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }


    private class ValueFilter extends Filter
    {
        //Invoked in a worker thread to filter the data according to the constraint.
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0)
            {
                Log.d("IF", "'" + constraint + "'");
                ArrayList<String> filterList = new ArrayList<>();
                resultado = new ArrayList<>();

                for (int i = 0; i < mStringFilterList.size(); i++)
                {
                    //Log.d("Value Adapter","Esto '"+mStringFilterList.get(i).toLowerCase()+"' contiene esto '"+constraint.toString().toLowerCase()+"' ?");
                    if (mStringFilterList.get(i).toLowerCase().contains(constraint.toString().toLowerCase()) || constraint.equals(""))
                    {
                        Log.d("Value Adapter", "SI! " + i);
                        filterList.add(mStringFilterList.get(i));
                        Log.d("ValueAdapter", "Se agrego " + mStringFilterList2.get(i));
                        resultado.add(mStringFilterList2.get(i));
                        //mStringFilterList2.add(mStringList2.get(i));
                    }
                }

                results.count = filterList.size();
                results.values = filterList;
            }
            else
            {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
                //mStringFilterList2 = mStringList2;
                resultado = mStringFilterList2;
                Log.d("ELSE", "resultado.size: '" + resultado.size() + "' - mStringFilterList2.size: '" + mStringFilterList2.size() + "' - mStringList2.size: '" + mStringList2.size() + "' ");
                Log.d("ValueAdapter", "Se agregaron " + mStringList2.size() + " elementos.");
            }

            return results;
        }

        //Invoked in the UI thread to publish the filtering results in the user interface.
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results)
        {
            //Log.d("publishResults", "OK " + resultado.get(0));
            mStringList = (ArrayList<String>) results.values;
            mStringList2 = resultado;
            notifyDataSetChanged();
        }

    }
}