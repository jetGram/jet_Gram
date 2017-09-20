package finalsoft.FcmService.Setting;

/**
 * Created by Pouya on 8/25/2016.
 */
public class hideChannelController {
    public static void add(Long id) {
        String m = Setting.getChannelHideList();
        m = m + "-" + String.valueOf(id);
        Setting.setChannelHideList(m);
    }

    public static void add(int id) {
        String m = Setting.getChannelHideList();
        m = m + "-" + String.valueOf(id);
        Setting.setChannelHideList(m);
    }

    public static void add(String user) {
        String m = Setting.getChannelHideList();
        m = m + "-" + String.valueOf(user);
        Setting.setChannelHideList(m);
    }

    public static Boolean is(String user) {
        try {
            if (Setting.getChannelHideList() == null || Setting.getChannelHideList().length() < 1)
                return false;
            boolean m = Setting.getChannelHideList().toLowerCase().contains(user.toLowerCase());
            return m;
        } catch (Exception e) {
            return false;
        }
    }

    public static Boolean is(Long id) {
        if (Setting.getChannelHideList() == null || Setting.getChannelHideList().length() < 1)
            return false;
        boolean m = Setting.getChannelHideList().toLowerCase().contains(String.valueOf(id));
        return m;
    }

    public static Boolean is(int id) {
        if (Setting.getChannelHideList() == null || Setting.getChannelHideList().length() < 1)
            return false;
        boolean m = Setting.getChannelHideList().toLowerCase().contains(String.valueOf(id));
//        Log.i("pushMode", "id:" + id + " result :" + m);
        return m;
    }
}
