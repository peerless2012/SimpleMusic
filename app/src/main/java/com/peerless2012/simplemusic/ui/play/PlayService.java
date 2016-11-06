package com.peerless2012.simplemusic.ui.play;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import java.util.List;

/**
 * @Author peerless2012
 * @Email peerless2012@126.com
 * @DateTime 2016/11/6 20:14
 * @Version V1.0
 * @Description: 播放的服务
 */
public class PlayService extends MediaBrowserServiceCompat{

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

}
