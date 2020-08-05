package com.creativedesign.PediTuRemis.AdminActivity;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.creativedesign.PediTuRemis.Objects.DriverObject;
import com.creativedesign.PediTuRemis.R;

import java.util.List;

public class DriversAdapter extends RecyclerView.Adapter<DriversAdapter.MyViewHolder> {

    private List<DriverObject> lstDrivers;
    private Context mContext;
    private DriversListener driversListener;
    private String numeroDocumento;

    public DriversAdapter(Context contex, List<DriverObject> lstDrivers, DriversListener driversListener) {
        this.mContext = contex;
        this.lstDrivers = lstDrivers;
        this.numeroDocumento = numeroDocumento;
        this.driversListener = driversListener;
    }

    @Override
    public DriversAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.driver_item, parent, false);
        return new DriversAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DriversAdapter.MyViewHolder holder, int position) {
        DriverObject driverObject = lstDrivers.get(position);
        holder.tv_driver.setText(driverObject.getName());
        try {
            holder.tv_trips_available.setText(String.valueOf(driverObject.getTripsAvailable()));
        } catch (Exception e) {
            holder.tv_trips_available.setText("0");
        }
        holder.tv_car.setText(driverObject.getCar());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tv_driver, tv_car;
        public EditText tv_trips_available;
        public ImageButton bt_menos, bt_mas;

        public MyViewHolder(View view) {
            super(view);
            tv_driver = view.findViewById(R.id.tv_driver);
            tv_car = view.findViewById(R.id.tv_car);
            tv_trips_available = view.findViewById(R.id.tv_trips_available);
            tv_trips_available.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        final DriverObject driverObject = lstDrivers.get(getAdapterPosition());
                        final int cantidadvalor = Integer.parseInt(s.toString());
                        driverObject.setTripsAvailable(cantidadvalor);
                        driversListener.updateDriverTrip(driverObject, cantidadvalor);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            bt_menos = view.findViewById(R.id.bt_menos);
            bt_mas = view.findViewById(R.id.bt_mas);
            bt_menos.setOnClickListener(this);
            bt_mas.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final DriverObject driverObject = lstDrivers.get(getAdapterPosition());
            if(v.getId() == R.id.bt_menos) {
                int cantidad = driverObject.getTripsAvailable() - 1;
                if(cantidad <= 0) {
                    cantidad = 0;
                }
                driverObject.setTripsAvailable(cantidad);
                tv_trips_available.setText(String.valueOf(cantidad));
            } else {
                int cantidad = driverObject.getTripsAvailable() + 1;
                driverObject.setTripsAvailable(cantidad);
                tv_trips_available.setText(String.valueOf(cantidad));
            }
        }
    }

    @Override
    public int getItemCount() {
        return lstDrivers.size();
    }

}
