package br.ufrj.caronae.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.Button;
import com.squareup.picasso.Picasso;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.RideFeedbackForJson;
import br.ufrj.caronae.models.modelsforjson.RideHistoryForJson;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RidesHistoryAdapter extends RecyclerView.Adapter<RidesHistoryAdapter.ViewHolder> {

    private final List<RideHistoryForJson> historyRides;
    private final MainAct activity;

    public RidesHistoryAdapter(List<RideHistoryForJson> historyRides, MainAct activity) {
        this.historyRides = historyRides;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_ridehistory, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(final RidesHistoryAdapter.ViewHolder holder, int position) {
        final RideHistoryForJson historyRide = historyRides.get(position);

        int color = 0, bgRes = 0;
        if (historyRide.getZone().equals("Centro")) {
            color = ContextCompat.getColor(activity, R.color.zone_centro);
            bgRes = R.drawable.bg_bt_raise_zone_centro;
        }
        if (historyRide.getZone().equals("Zona Sul")) {
            color = ContextCompat.getColor(activity, R.color.zone_sul);
            bgRes = R.drawable.bg_bt_raise_zone_sul;
        }
        if (historyRide.getZone().equals("Zona Oeste")) {
            color = ContextCompat.getColor(activity, R.color.zone_oeste);
            bgRes = R.drawable.bg_bt_raise_zone_oeste;
        }
        if (historyRide.getZone().equals("Zona Norte")) {
            color = ContextCompat.getColor(activity, R.color.zone_norte);
            bgRes = R.drawable.bg_bt_raise_zone_norte;
        }
        if (historyRide.getZone().equals("Baixada")) {
            color = ContextCompat.getColor(activity, R.color.zone_baixada);
            bgRes = R.drawable.bg_bt_raise_zone_baixada;
        }
        if (historyRide.getZone().equals("Grande Niterói")) {
            color = ContextCompat.getColor(activity, R.color.zone_niteroi);
            bgRes = R.drawable.bg_bt_raise_zone_niteroi;
        }
        if (historyRide.getZone().equals("Outros")) {
            color = ContextCompat.getColor(activity, R.color.zone_outros);
            bgRes = R.drawable.bg_bt_raise_zone_outros;
        }

        if (historyRide.isGoing())
            holder.time_tv.setText(activity.getString(R.string.arrivedAt, historyRide.getTime() + " | "));
        else
            holder.time_tv.setText(activity.getString(R.string.leftAt, historyRide.getTime() + " | "));
        holder.time_tv.setTextColor(color);
        holder.date_tv.setText(Util.formatDateRemoveYear(historyRide.getDate()));
        holder.date_tv.setTextColor(color);
        holder.name_tv.setTextColor(color);
        String location;
        if (historyRide.isGoing()) {
            location = historyRide.getNeighborhood() + " ➜ " + historyRide.getHub();
        } else {
            location = historyRide.getHub() + " ➜ " + historyRide.getNeighborhood();
        }
        holder.location_tv.setText(location);
        holder.location_tv.setTextColor(color);

        User driver = historyRide.getDriver();
        if (driver != null) {
            holder.name_tv.setText(driver.getName());
            String driverPic = driver.getProfilePicUrl();
            if (driverPic != null && !driverPic.isEmpty()) {
                Picasso.with(activity).load(driverPic)
                        .placeholder(R.drawable.user_pic)
                        .error(R.drawable.user_pic)
                        .transform(new RoundedTransformation())
                        .into(holder.photo_iv);
            } else {
                holder.photo_iv.setImageResource(R.drawable.user_pic);
            }
        }
        if (historyRide.getFeedback() != null) {
            holder.feedback_bt.setVisibility(View.INVISIBLE);
        } else {
            holder.feedback_bt.setVisibility(View.VISIBLE);
            holder.feedback_bt.setBackgroundResource(bgRes);
            holder.feedback_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight){

                        @Override
                        protected void onBuildDone(Dialog dialog) {
                            dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        }

                        @Override
                        public void onPositiveActionClicked(DialogFragment fragment) {
                            saveFeedback("good", historyRide);

                            super.onPositiveActionClicked(fragment);
                            holder.feedback_bt.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onNegativeActionClicked(DialogFragment fragment) {
                            saveFeedback("bad", historyRide);

                            super.onNegativeActionClicked(fragment);
                            holder.feedback_bt.setVisibility(View.INVISIBLE);
                        }
                    };

                    ((SimpleDialog.Builder) builder).message(activity.getString(R.string.rideHistory_DialogMsg))
                            .title(activity.getString(R.string.rideHistory_DialogTitle))
                            .positiveAction(activity.getString(R.string.yes))
                            .negativeAction(activity.getString(R.string.no));

                    DialogFragment fragment = DialogFragment.newInstance(builder);
                    fragment.show(activity.getSupportFragmentManager(), null);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return historyRides.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView time_tv;
        public TextView location_tv;
        public TextView name_tv;
        public TextView date_tv;
        public ImageView photo_iv;
        public Button feedback_bt;

        public ViewHolder(View itemView) {
            super(itemView);

            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            location_tv = (TextView) itemView.findViewById(R.id.location_tv);
            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
            date_tv = (TextView) itemView.findViewById(R.id.date_tv);
            photo_iv = (ImageView) itemView.findViewById(R.id.photo_iv);
            feedback_bt = (Button) itemView.findViewById(R.id.feedback_bt);
        }
    }

    private void saveFeedback(final String rate, final RideHistoryForJson historyRide){
            App.getNetworkService(activity.getApplicationContext()).saveFeedback(new RideFeedbackForJson(App.getUser().getDbId(), historyRide.getDbId(), "good"))
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Util.toast(activity.getString(R.string.rideHistory_Feedback));
                                Log.i("saveFeedback", "rated " + rate + ", ride id = " + historyRide.getDbId());
                            } else {
                                Util.treatResponseFromServer(response);
                                Util.toast(activity.getString(R.string.errorRideHistory_Feedback));
                                Log.e("saveFeedback", response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Util.toast(activity.getString(R.string.errorRideHistory_Feedback));
                            Log.e("saveFeedback", t.getMessage());
                        }
                    });

    }
}
