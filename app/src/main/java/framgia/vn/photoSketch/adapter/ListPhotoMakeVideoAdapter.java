package framgia.vn.photoSketch.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import framgia.vn.photoSketch.R;
import framgia.vn.photoSketch.activity.GalleryActivity;
import framgia.vn.photoSketch.activity.MakeVideoActivity;
import framgia.vn.photoSketch.constants.ConstActivity;
import framgia.vn.photoSketch.models.Photo;

/**
 * Created by nghicv on 02/05/2016.
 */
public class ListPhotoMakeVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Photo> mPhotos;
    private final int VIEW_TYPE_ITEM_IMAGE = 0;
    private final int VIEW_TYPE_ITEM_ADD_IMAGE = 1;
    private Context mContext;
    private int selectedIndex = 0;
    private OnItemSelectListener mOnItemSelectListener;

    public ListPhotoMakeVideoAdapter(List<Photo> photos) {
        mPhotos = photos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null)
            mContext = parent.getContext();
        if(viewType == VIEW_TYPE_ITEM_IMAGE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_image_make_video, null);
            return new ViewHolderItemImage(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_add_photo, null);
            return new ViewHolderItemAddImage(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (position != mPhotos.size()) {
            final ViewHolderItemImage viewHolder = (ViewHolderItemImage) holder;
            Photo photo = mPhotos.get(position);
            Uri uri = Uri.fromFile(new File(photo.getUri()));
            Picasso.with(mContext)
                    .load(uri)
                    .resize(ConstActivity.PICASSO_IMAGE_RESIZE_WIDTH, ConstActivity.PICASSO_IMAGE_RESIZE_HEIGHT)
                    .centerCrop()
                    .into(viewHolder.imageView);
            if(selectedIndex == position) {
                viewHolder.relativeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.layout_main_bottom_holder_background));
            } else {
                viewHolder.relativeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.cardview_dark_background));
            }
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedIndex = position;
                    if (mOnItemSelectListener != null) {
                        mOnItemSelectListener.onSelected(position);
                    }
                    notifyDataSetChanged();
                }
            });
        } else {
            ViewHolderItemAddImage viewHolder = (ViewHolderItemAddImage) holder;
            viewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GalleryActivity.class);
                    ((MakeVideoActivity)mContext).startActivityForResult(intent, MakeVideoActivity.REQUEST_CODE);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mPhotos.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == mPhotos.size() ? VIEW_TYPE_ITEM_ADD_IMAGE : VIEW_TYPE_ITEM_IMAGE;
    }

    public void onItemDismiss(int position) {
        if(position >= mPhotos.size()) {
            notifyDataSetChanged();
            return;
        }
        mPhotos.remove(position);
        if(mOnItemSelectListener == null) {
            return;
        }
        if (selectedIndex == position) {
            if (position != mPhotos.size() && mPhotos.size() != 0) {
                mOnItemSelectListener.onSelected(selectedIndex);
                notifyDataSetChanged();
                return;
            }
            if (position == mPhotos.size()) {
                selectedIndex = selectedIndex - 1;
                mOnItemSelectListener.onSelected(selectedIndex);
                notifyDataSetChanged();
                return;
            }
            mOnItemSelectListener.onSelected(0);
        } else if (position < selectedIndex) {
            selectedIndex = selectedIndex - 1;
            mOnItemSelectListener.onSelected(selectedIndex);
        }
        notifyItemRemoved(position);
    }

    public void onItemMove(int fromPosition, int toPosition) {
        if(toPosition == mPhotos.size() || fromPosition == mPhotos.size()) {
            return;
        }
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mPhotos, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mPhotos, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        if(mOnItemSelectListener != null && selectedIndex == fromPosition) {
            selectedIndex = toPosition;
            mOnItemSelectListener.onSelected(toPosition);
        }
    }


    static class ViewHolderItemImage extends RecyclerView.ViewHolder {
        ImageView imageView;
        RelativeLayout relativeLayout;
        public ViewHolderItemImage(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_make_video);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative_image_make_video);
        }
    }

    static class ViewHolderItemAddImage extends RecyclerView.ViewHolder {
        FloatingActionButton btnAdd;
        public ViewHolderItemAddImage(View itemView) {
            super(itemView);
            btnAdd = (FloatingActionButton) itemView.findViewById(R.id.btn_add_image);
        }
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        mOnItemSelectListener = onItemSelectListener;
    }

    public interface OnItemSelectListener {
        void onSelected(int position);
    }
}
