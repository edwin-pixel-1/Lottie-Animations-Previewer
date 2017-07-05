package com.cobos.edwin.lottieloader.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cobos.edwin.lottieloader.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edwin-Cobos - alejo740@gmail.com
 */

public class FilesAdapter extends RecyclerView.Adapter {

    public interface ItemClickListener {
        void onClickItemList(File file);
    }

    private List<File> listData;
    private ItemClickListener clickListener;

    public FilesAdapter() {
        listData = new ArrayList<>();
    }

    public void setListData(List<File> files) {
        listData.clear();
        listData.addAll(files);
        notifyDataSetChanged();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(v, clickListener);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FileViewHolder vh = (FileViewHolder) holder;
        vh.bind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title;
        private ImageView image;
        private ItemClickListener clickListener;
        private File file;

        public FileViewHolder(View view, ItemClickListener clickListener) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            image = (ImageView) view.findViewById(R.id.image);
            if (clickListener != null) {
                this.clickListener = clickListener;
            }
        }

        public void bind(File file) {
            this.file = file;
            title.setText(file.getName());

            if (file.isDirectory()) {
                image.setImageResource(R.drawable.ic_folder);
            } else {
                image.setImageResource(R.drawable.ic_json);
            }

            if(checkFolder()) {
                itemView.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onClickItemList(file);
            }
        }

        private boolean checkFolder() {
            if(file.isDirectory()) {
                File[] files = file.listFiles();
                boolean validDirectory = false;
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory() || files[i].getName().toLowerCase().endsWith(".json")) {
                        validDirectory = true;
                        break;
                    }
                }
                return validDirectory;
            }
            return true;
        }
    }
}
