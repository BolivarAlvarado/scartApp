package Modelo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.espol.artauthenticator.R;

import java.util.List;

public class ImagenAdapter extends RecyclerView.Adapter<ImagenAdapter.ImagenViewHolder> {

    private List<Imagen> listaImagenes;

    public ImagenAdapter(List<Imagen> listaImagenes) {
        this.listaImagenes = listaImagenes;
    }

    @NonNull
    @Override
    public ImagenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_imagen, parent, false);
        return new ImagenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagenViewHolder holder, int position) {
        Imagen imagen = listaImagenes.get(position);
        holder.nombreObra.setText(imagen.getNombre());

        // Cargar la imagen usando Glide
        Glide.with(holder.itemView.getContext())
                .load(imagen.getRutaImagen())
                .placeholder(R.drawable.placeholder)  // Imagen de marcador de posición
                .error(R.drawable.error_image)       // Imagen en caso de error
                .into(holder.imagenObra);
    }

    @Override
    public int getItemCount() {
        return listaImagenes.size();
    }

    public void updateData(List<Imagen> nuevasImagenes) {
        listaImagenes.clear();
        listaImagenes.addAll(nuevasImagenes);
        notifyDataSetChanged();
    }

    public static class ImagenViewHolder extends RecyclerView.ViewHolder {

        ImageView imagenObra;
        TextView nombreObra;

        public ImagenViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenObra = itemView.findViewById(R.id.imageViewObra);
            nombreObra = itemView.findViewById(R.id.textViewNombreObra);
        }
    }
}














