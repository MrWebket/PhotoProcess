
package com.shenghuoli.library.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;

/**
 * <p>
 * 类描述：网络状态工具类,需要权限{@link android.Manifest.permission #ACCESS_NETWORK_STATE}
 * 
 * @author dbzhuang
 */
public class NetworkUtil {
    private NetworkUtil() {
    }

    /**
     * 判断网络是否可用
     * 
     * @param context
     * @return
     */
    public static boolean isNetworkAvaiable(Context context) {
        ConnectivityManager connMgr = getConnectivityManager(context);
        if (connMgr == null) {
            return false;
        }
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            return false;
        }
        return true;
    }

    /**
     * 判断网络是否连接
     * 
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connMgr = getConnectivityManager(context);
        if (connMgr == null) {
            return false;
        }
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            return false;
        }
        return true;
    }

    /**
     * 获取网络连接管理
     * 
     * @param context
     * @return
     */
    private static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * 获取当前的网络状态 -1：没有网络 1：net网络、2:wap网络 、 3：WIFI网络
     * 
     * @param context
     * @return NetType
     */
    public static NetType getAPNType(Context context) {
        ConnectivityManager connMgr = getConnectivityManager(context);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo == null) {
            return null;
        }
        NetType netType = new NetType();
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            // 联通：3gnet/3gwap/uninet/uniwap
            // 移动：cmnet/cmwap
            // 电信：ctnet/ctwap
            String extraInfo = networkInfo.getExtraInfo().toLowerCase(Locale.CHINA);
            if (!TextUtils.isEmpty(extraInfo)) {
                if (extraInfo.equals("cmwap") || extraInfo.equals("3gwap")
                        || extraInfo.equals("ctwap") || extraInfo.equals("uniwap")) {
                    netType.type = NetType.MOBILE_WAP;
                    LogUtil.error(NetworkUtil.class,"手机网络：wap");
                } else {
                    netType.type = NetType.MOBILE_NET;
                    LogUtil.error(NetworkUtil.class,"手机网络：net");
                }
                netType.extraInfo = extraInfo;
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType.type = NetType.WIFI;
            netType.extraInfo = null;
            LogUtil.error(NetworkUtil.class,"wifi网络");
        }
        return netType;
    }

    /**
     * 包含网络类型和其附加信息的实体类
     */
    public static class NetType implements Parcelable {
        /**
         * net网络
         */
        public final static int MOBILE_NET = 0x0001;

        /**
         * wap网络
         */
        public final static int MOBILE_WAP = 0x0002;

        /**
         * wifi类型
         */
        public final static int WIFI = 0x0003;

        public int type = -1;

        public String extraInfo = null;

        /*
         * (non-Javadoc)
         * @see android.os.Parcelable#describeContents()
         */
        @Override
        public int describeContents() {
            return 0;
        }

        /*
         * (non-Javadoc)
         * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
         */
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(type);
            dest.writeString(extraInfo);
        }

        public static final Creator<NetType> CREATOR = new Creator<NetType>() {
            public NetType createFromParcel(Parcel in) {
                NetType type = new NetType();
                type.type = in.readInt();
                type.extraInfo = in.readString();
                return type;
            }

            public NetType[] newArray(int size) {
                return new NetType[size];
            }
        };
    }

    /**
     * 获取android设备的ip地址
     * 
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            LogUtil.error(NetworkUtil.class,ex.toString());
        }
        return null;
    }
}
