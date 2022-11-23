package fr.corenting.convertisseureurofranc

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter


class DropdownAdapter<T>(context: Context, resource: Int, objects: List<T>) :
    ArrayAdapter<T>(context, resource, objects) {

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                return FilterResults()
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {}
        }
    }
}
