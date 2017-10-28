package br.ufrj.caronae.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.ufrj.caronae.R;

/**
 * Created by Luis on 10/24/2017.
 */

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.ViewHolder> {

    private final short TEXT_COLUMN = 0;
    private final short COLOR_COLUMN = 1;

    String[] textList;
    Context context;

    /**
     * Receive a String[] with the texts and a int[] with the colors R.color.xxx, if values of color id
     * * is null or the color array is shorter than text array the color will be set transparent and check will
     * * be set to darker_gray
     **/
    public CustomListAdapter(String[] textList) {
        this.textList = textList;
    }

    @Override
    public CustomListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contentView = inflater.inflate(R.layout.selector_item, parent, false);

        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(final CustomListAdapter.ViewHolder holder, final int position) {
        holder.text.setText(textList[position]);
    }

    private void setKeyByType() {
//        SharedPref.saveDialogTypePref(SharedPref.DIALOG_DISMISS_KEY, type);
    }

    private static void updateCheckerVisibility(ImageView checker, boolean visible) {
        if (visible) {
            checker.setVisibility(View.VISIBLE);
            return;
        }
        checker.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return textList.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView colorTab;
        private TextView text;
        private ImageView checkImage;
        RelativeLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);

            colorTab = (ImageView) itemView.findViewById(R.id.color_bar);
            text = (TextView) itemView.findViewById(R.id.text);
            checkImage = (ImageView) itemView.findViewById(R.id.check_icon);
            layout = (RelativeLayout) itemView.findViewById(R.id.main_layout);
        }
    }
}
