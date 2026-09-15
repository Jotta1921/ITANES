package pe.josueyovera.itanes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import pe.josueyovera.itanes.data.local.entity.PlaceEntity;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private List<PlaceEntity> places = new ArrayList<>();
    private OnPlaceClickListener listener;

    public interface OnPlaceClickListener {
        void onPlaceClick(int placeId);
    }

    public void setPlaces(List<PlaceEntity> places) {
        this.places = places;
        notifyDataSetChanged();
    }

    public void setOnPlaceClickListener(OnPlaceClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        PlaceEntity place = places.get(position);
        holder.textName.setText(place.getName());
        holder.textDescription.setText(place.getShortDescription());
        
        Glide.with(holder.itemView.getContext())
                .load(place.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .centerCrop()
                .into(holder.imagePlace);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlaceClick(place.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePlace;
        TextView textName;
        TextView textDescription;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePlace = itemView.findViewById(R.id.imagePlace);
            textName = itemView.findViewById(R.id.textPlaceName);
            textDescription = itemView.findViewById(R.id.textPlaceDescription);
        }
    }
}
