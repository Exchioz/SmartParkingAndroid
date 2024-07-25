package com.example.bookingparkir.History

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.bookingparkir.API.FinishedReservation
import com.example.bookingparkir.API.OngoingReservation
import com.example.bookingparkir.R

class OngoingReservationsAdapter(private val context: Context, private val reservations: List<OngoingReservation>) : ArrayAdapter<OngoingReservation>(context, 0, reservations) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        val viewHolder: ViewHolder

        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_ongoing_history, parent, false)
            viewHolder = ViewHolder()
            viewHolder.textViewReservasiId = itemView.findViewById(R.id.textViewReservasiId)
            viewHolder.textViewStatus = itemView.findViewById(R.id.textViewStatus)
            viewHolder.textViewNamaParkir = itemView.findViewById(R.id.textViewNamaParkir)
            viewHolder.textViewAlamatParkir = itemView.findViewById(R.id.textViewAlamatParkir)
            viewHolder.textViewLamaWaktu = itemView.findViewById(R.id.textViewLamaWaktu)
            viewHolder.textViewTotalBiaya = itemView.findViewById(R.id.textViewTotalBiaya)
            viewHolder.textViewWaktu = itemView.findViewById(R.id.textViewWaktu)
            viewHolder.textViewInfo = itemView.findViewById(R.id.textViewInfo)


            itemView.tag = viewHolder
        } else {
            viewHolder = itemView.tag as ViewHolder
        }

        val reservation = reservations[position]

        viewHolder.textViewReservasiId?.text = reservation.reservasi_id.toString()
        viewHolder.textViewStatus?.text = reservation.status
        viewHolder.textViewNamaParkir?.text = reservation.nama_tempat_parkir
        viewHolder.textViewAlamatParkir?.text = reservation.alamat_tempat_parkir

        if (reservation.status == "Pending" || reservation.status == "Checkout"){
            viewHolder.textViewInfo?.text = "Expired"
            viewHolder.textViewWaktu?.text = reservation.waktu_akhir
        } else if (reservation.status == "Active"){
            viewHolder.textViewInfo?.text = "Entry at"
            viewHolder.textViewWaktu?.text = reservation.waktu_awal
        }

        return itemView!!
    }

    private class ViewHolder {
        var textViewReservasiId: TextView? = null
        var textViewStatus: TextView? = null
        var textViewNamaParkir: TextView? = null
        var textViewAlamatParkir: TextView? = null
        var textViewLamaWaktu: TextView? = null
        var textViewTotalBiaya: TextView? = null
        var textViewWaktu: TextView? = null
        var textViewInfo: TextView? = null
    }
}
