/***
 Copyright (c) 2015-2016 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.dragdrop;

import android.content.ClipData;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.io.File;

class RowController extends RecyclerView.ViewHolder
    implements View.OnClickListener, View.OnLongClickListener {
  interface OnStartDragListener {
    void onStartDrag();
  }

  final private TextView title;
  final private ImageView thumbnail;
  private Uri videoUri=null;
  private String videoMimeType=null;
  final private OnStartDragListener listener;

  RowController(View row, OnStartDragListener listener) {
    super(row);

    this.listener=listener;
    title=(TextView)row.findViewById(android.R.id.text1);
    thumbnail=(ImageView)row.findViewById(R.id.thumbnail);

    row.setOnClickListener(this);
    row.setOnLongClickListener(this);
  }

  @Override
  public void onClick(View v) {
    Intent i=new Intent(Intent.ACTION_VIEW);

    i.setDataAndType(videoUri, videoMimeType);
    title.getContext().startActivity(i);
  }

  @Override
  public boolean onLongClick(View v) {
    if (listener!=null) {
      listener.onStartDrag();
    }

    ClipData clip=ClipData.newRawUri(title.getText(), videoUri);
    View.DragShadowBuilder shadow=new View.DragShadowBuilder(thumbnail);

    itemView.startDrag(clip, shadow, null, 0);

    return(true);
  }

  void bindModel(Cursor row) {
    title.setText(row.getString(
      row.getColumnIndex(MediaStore.Video.Media.TITLE)));

    videoUri=
        ContentUris.withAppendedId(
          MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
          row.getInt(row.getColumnIndex(MediaStore.Video.Media._ID)));

    Picasso.with(thumbnail.getContext())
      .load(videoUri.toString())
      .fit().centerCrop()
      .placeholder(R.drawable.ic_media_video_poster)
      .into(thumbnail);

    int mimeTypeColumn=
        row.getColumnIndex(MediaStore.Video.Media.MIME_TYPE);

    videoMimeType=row.getString(mimeTypeColumn);
  }
}
