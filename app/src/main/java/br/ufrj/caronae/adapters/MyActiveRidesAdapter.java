package br.ufrj.caronae.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.ActiveRideAct;
import br.ufrj.caronae.acts.ChatAct;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.acts.ProfileAct;
import br.ufrj.caronae.models.ChatAssets;
import br.ufrj.caronae.models.NewChatMsgIndicator;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyActiveRidesAdapter extends RecyclerView.Adapter<MyActiveRidesAdapter.ViewHolder> {

    private final List<RideForJson> ridesList;
    private final MainAct activity;
    private final List<NewChatMsgIndicator> newChatMsgIndicatorList;

    public MyActiveRidesAdapter(List<RideForJson> ridesList, MainAct activity) {
        this.ridesList = ridesList;
        this.activity = activity;

        newChatMsgIndicatorList = NewChatMsgIndicator.listAll(NewChatMsgIndicator.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_myactiveride, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final RideForJson rideOffer = ridesList.get(position);

        int color = ContextCompat.getColor(activity, R.color.zone_outros);
        int bgRes = R.drawable.bg_bt_raise_zone_outros;

        if (rideOffer.getZone().equals("Centro")) {
            color = ContextCompat.getColor(activity, R.color.zone_centro);
            bgRes = R.drawable.bg_bt_raise_zone_centro;
        }
        if (rideOffer.getZone().equals("Zona Sul")) {
            color = ContextCompat.getColor(activity, R.color.zone_sul);
            bgRes = R.drawable.bg_bt_raise_zone_sul;
        }
        if (rideOffer.getZone().equals("Zona Oeste")) {
            color = ContextCompat.getColor(activity, R.color.zone_oeste);
            bgRes = R.drawable.bg_bt_raise_zone_oeste;
        }
        if (rideOffer.getZone().equals("Zona Norte")) {
            color = ContextCompat.getColor(activity, R.color.zone_norte);
            bgRes = R.drawable.bg_bt_raise_zone_norte;
        }
        if (rideOffer.getZone().equals("Baixada")) {
            color = ContextCompat.getColor(activity, R.color.zone_baixada);
            bgRes = R.drawable.bg_bt_raise_zone_baixada;
        }
        if (rideOffer.getZone().equals("Grande Niterói")) {
            color = ContextCompat.getColor(activity, R.color.zone_niteroi);
            bgRes = R.drawable.bg_bt_raise_zone_niteroi;
        }
        viewHolder.location_tv.setTextColor(color);

        String profilePicUrl = rideOffer.getDriver().getProfilePicUrl();
        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Picasso.with(activity).load(profilePicUrl)
                    .placeholder(R.drawable.user_pic)
                    .error(R.drawable.user_pic)
                    .transform(new RoundedTransformation())
                    .into(viewHolder.photo_iv);
        }

        if (App.getUser().getDbId() != rideOffer.getDriver().getDbId())
            viewHolder.photo_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, ProfileAct.class);
                    intent.putExtra("user", new Gson().toJson(rideOffer.getDriver()));
                    intent.putExtra("from", "myactiveridesadapter");
                    activity.startActivity(intent);
                }
            });

        String timeText;
        if (rideOffer.isGoing())
            timeText = activity.getResources().getString(R.string.arrivingAt, Util.formatTime(rideOffer.getTime()));
        else
            timeText = activity.getResources().getString(R.string.leavingAt, Util.formatTime(rideOffer.getTime()));
        viewHolder.time_tv.setText(timeText);
        viewHolder.date_tv.setText(Util.formatBadDateWithoutYear(rideOffer.getDate()));
        viewHolder.name_tv.setText(rideOffer.getDriver().getName());
        String location;
        if (rideOffer.isGoing())
            location = rideOffer.getNeighborhood() + " ➜ " + rideOffer.getHub();
        else
            location = rideOffer.getHub() + " ➜ " + rideOffer.getNeighborhood();
        viewHolder.location_tv.setText(location);

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewChatMsgIndicator.deleteAll(NewChatMsgIndicator.class, "db_id = ?", rideOffer.getDbId()+"");
                viewHolder.newMsgIndicator_iv.setVisibility(View.INVISIBLE);
                int colorChat = ContextCompat.getColor(activity, R.color.gray);
                viewHolder.newMsgIndicator_iv.setColorFilter(colorChat);

                Intent intent = new Intent(activity, ActiveRideAct.class);
                intent.putExtra("ride", rideOffer);
                activity.startActivity(intent);
            }
        });

        final int finalColor = color;
        final int finalBgRes = bgRes;
        final String finallocation = location;
        viewHolder.newMsgIndicator_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ChatAssets> l = ChatAssets.find(ChatAssets.class, "ride_id = ?", rideOffer.getDbId() + "");
                if (l == null || l.isEmpty())
                    new ChatAssets(rideOffer.getDbId() + "", finallocation, finalColor, finalBgRes,
                            Util.formatBadDateWithoutYear(rideOffer.getDate()),
                            Util.formatTime(rideOffer.getTime())).save();

                Intent intent = new Intent(activity, ChatAct.class);
                intent.putExtra("rideId", rideOffer.getDbId() + "");
                activity.startActivity(intent);
            }
        });

        boolean found = false;
        for (NewChatMsgIndicator newChatMsgIndicator : newChatMsgIndicatorList) {
            if (newChatMsgIndicator.getDbId() == rideOffer.getDbId()) {
                viewHolder.newMsgIndicator_iv.setVisibility(View.VISIBLE);
                found = true;
                break;
            }
        }

        if (!found) {
            int colorChat = ContextCompat.getColor(activity, R.color.gray);
            viewHolder.newMsgIndicator_iv.setColorFilter(colorChat);
            viewHolder.newMsgIndicator_iv.setVisibility(View.INVISIBLE);
        }
    }

    public void remove(int rideId) {
        for (int i = 0; i < ridesList.size(); i++)
            if (ridesList.get(i).getDbId() == rideId) {
                ridesList.remove(i);
                notifyItemRemoved(i);
                return;
            }
    }

    @Override
    public int getItemCount() {
        return ridesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView photo_iv;
        public TextView time_tv;
        public TextView date_tv;
        public TextView name_tv;
        public TextView location_tv;
        public CardView cardView;
        public ImageView newMsgIndicator_iv;

        public ViewHolder(View itemView) {
            super(itemView);

            photo_iv = (CircleImageView) itemView.findViewById(R.id.photo_iv);
            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            date_tv = (TextView) itemView.findViewById(R.id.date_tv);
            location_tv = (TextView) itemView.findViewById(R.id.location_tv);
            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            newMsgIndicator_iv = (ImageView) itemView.findViewById(R.id.newMsgIndicator_iv);
        }
    }
}
