package com.example.bookingparkir.ListViewPark

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.bookingparkir.API.PropertyModel
import com.example.bookingparkir.R

class PropertyListAdapter(private val context: Context, private val dataSource: List<PropertyModel>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_property, parent, false)
            holder = ViewHolder()
            holder.nameTextView = view.findViewById(R.id.propertyName)
            holder.addressTextView = view.findViewById(R.id.propertyAddress)
            holder.priceTextView = view.findViewById(R.id.propertyPrice)
            holder.parkingTextView = view.findViewById(R.id.propertyParking)
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val propertyModel = getItem(position) as PropertyModel
        holder.nameTextView?.text = propertyModel.name
        holder.addressTextView?.text = propertyModel.address
        holder.priceTextView?.text = propertyModel.price.toString()
        holder.parkingTextView?.text = propertyModel.parking.toString()

        // Disable click jika jumlah parkir tersedia sudah 0
        if (propertyModel.parking <= 0) {
            view.isEnabled = false
            view.setOnClickListener(null)  // Menghapus onClickListener
            // Opsi tambahan: Mengubah tampilan untuk menandakan bahwa item tidak aktif
            view.alpha = 0.5f  // Membuat item terlihat lebih pudar
        } else {
            view.isEnabled = true
            view.alpha = 1.0f  // Mengembalikan tampilan item ke keadaan normal
            // Tetapkan onClickListener jika item dapat diklik
            view.setOnClickListener {
                val intent = Intent(context, DetailReserveActivity::class.java).apply {
                    putExtra("idTempatParkir", propertyModel.id)
                }
                context.startActivity(intent)
            }
        }

        return view
    }


    private class ViewHolder {
        var nameTextView: TextView? = null
        var addressTextView: TextView? = null
        var priceTextView: TextView? = null
        var parkingTextView: TextView? = null
    }
}
