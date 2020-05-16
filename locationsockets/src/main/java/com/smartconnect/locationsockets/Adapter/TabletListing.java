package com.smartconnect.locationsockets.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.smartconnect.locationsockets.Model.TabListing;
import com.smartconnect.locationsockets.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.smartconnect.locationsockets.Constants.APIConst.EVENT_TABLET_REMOVE;
import static com.smartconnect.locationsockets.Service.SocketService.stream;


public class TabletListing extends RecyclerView.Adapter<TabletListing.ProductViewHolder> {

    private Context mCtx;
    private List<TabListing> tabletList;

    public TabletListing(Context mCtx, List<TabListing> tabletList) {
        this.mCtx = mCtx;
        this.tabletList = tabletList;
    }

    @Override
    public TabletListing.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.activity_listview, null);
        return new TabletListing.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TabletListing.ProductViewHolder holder, int position) {
        final TabListing bh = tabletList.get(position);
        holder.title.setText(bh.getRoomName());
        holder.subtitle.setText(bh.getRoomLocation());
        if (bh.getOnlineStatus()) {
           // holder.icon.setBackgroundResource(R.drawable.green);
        } else {
           // holder.icon.setBackgroundResource(R.drawable.red);
        }

        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mCtx, "Show Details of Conference Room", Toast.LENGTH_SHORT).show();
            }
        });

        holder.ll_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Toast.makeText(mCtx, "Long Click", Toast.LENGTH_SHORT).show();

                new AlertDialog.Builder(mCtx)
                        .setTitle("Remove Conference Room")
                        .setMessage("You can remove conference room tablet")
                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Your code
                                Toast.makeText(mCtx, "Remove Tablet", Toast.LENGTH_SHORT).show();
                                try {
                                    JSONObject jsonAdd = new JSONObject();
                                    jsonAdd.put("udid", bh.getUUID());

                                    JSONObject jsonObj = new JSONObject();
                                    jsonObj.put("eventName", EVENT_TABLET_REMOVE);
                                    jsonObj.put("data", jsonAdd);

                                    stream.emit("request", jsonObj);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
//                                stream.emit("deleteRoom",)
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                // Toast.makeText(mCtx, "Cancel", Toast.LENGTH_SHORT).show();
                            }
                        })
//                        .setNeutralButton("")
                        .show();


                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return tabletList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView title, subtitle;
        ImageView icon;
        LinearLayout ll_item;

        ProductViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            ll_item = (LinearLayout) itemView.findViewById(R.id.ll_item);
        }
    }
}
