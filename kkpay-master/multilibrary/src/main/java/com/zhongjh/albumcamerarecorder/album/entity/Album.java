/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhongjh.albumcamerarecorder.album.entity;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.zhongjh.albumcamerarecorder.R;
import com.zhongjh.albumcamerarecorder.album.loader.AlbumLoader;

/**
 * 专辑
 * @author zhihu
 */
public class Album implements Parcelable {
    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @NonNull
        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
    public static final String ALBUM_ID_ALL = String.valueOf(-1);
    public static final String ALBUM_NAME_ALL = "All";

    private final String mId;
    private final Uri mCoverUri;
    private final String mDisplayName;
    private long mCount;

    Album(String id, Uri coverUri, String albumName, long count) {
        mId = id;
        mCoverUri = coverUri;
        mDisplayName = albumName;
        mCount = count;
    }

    Album(Parcel source) {
        mId = source.readString();
        mCoverUri = source.readParcelable(Uri.class.getClassLoader());
        mDisplayName = source.readString();
        mCount = source.readLong();
    }

    /**
     * {@link Cursor} 构建一个新的实体 {@link Album}
     * 此方法不负责管理光标资源，如关闭、迭代等。
     */
    public static Album valueOf(Cursor cursor) {
        String column = cursor.getString(cursor.getColumnIndex(AlbumLoader.COLUMN_URI));
        return new Album(
                cursor.getString(cursor.getColumnIndex("bucket_id")),
                Uri.parse(column != null ? column : ""),
                cursor.getString(cursor.getColumnIndex("bucket_display_name")),
                cursor.getLong(cursor.getColumnIndex(AlbumLoader.COLUMN_COUNT)));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeParcelable(mCoverUri, 0);
        dest.writeString(mDisplayName);
        dest.writeLong(mCount);
    }

    public String getId() {
        return mId;
    }

    public Uri getCoverUri() {
        return mCoverUri;
    }

    public long getCount() {
        return mCount;
    }

    /**
     *
     * 数量添加一个，目前是考虑如果有拍照功能，就数量+1
     * @deprecated 作废，拍照已经独立出来
     */
    public void addCaptureCount() {
        mCount++;
    }

    /**
     * 显示名称，可能返回“全部”
     * @return 返回名称
     */
    public String getDisplayName(Context context) {
        if (isAll()) {
            return context.getString(R.string.z_multi_library_album_name_all);
        }
        return mDisplayName;
    }

    /**
     * 判断如果id = -1的话，就是查询全部的意思
     * @return 是否全部
     */
    public boolean isAll() {
        return ALBUM_ID_ALL.equals(mId);
    }

    public boolean isEmpty() {
        return mCount == 0;
    }

}