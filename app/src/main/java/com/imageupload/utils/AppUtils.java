
package com.imageupload.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.MediaColumns;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Provides convenience methods and abstractions to some tasks in Android
 * <p/>
 * <br/>
 * <br/>
 *
 * @author
 */
public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();

    static ProgressDialog mProgressDialog;
    private static AppUtils appUtils;
    private Toast toast;


    public static AppUtils getInstance() {
        if (appUtils == null) {
            appUtils = new AppUtils();
        }
        return appUtils;
    }

    /**
     * Shows a long time duration toast message.
     *
     * @param msg Message to be show in the toast.
     * @return Toast object just shown
     **/
    public Toast showToast(Context ctx, CharSequence msg) {
        return showToast(ctx, msg, Toast.LENGTH_LONG);
    }

    /**
     * Shows the message passed in the parameter in the Toast.
     *
     * @param msg      Message to be show in the toast.
     * @param duration Duration in milliseconds for which the toast should be shown
     * @return Toast object just shown
     **/
    public Toast showToast(Context ctx, CharSequence msg, int duration) {
        if (toast!=null)
            toast.cancel();

        toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);
        toast.setDuration(duration);
        toast.show();
        return toast;
    }

    /**
     * Checks if the Internet connection is available.
     *
     * @return Returns true if the Internet connection is available. False otherwise.
     **/
    public  boolean isInternetAvailable(Context ctx) {
        // using received context (typically activity) to get SystemService causes memory link as this holds strong reference to that activity.
        // use application level context instead, which is available until the app dies.
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // if network is NOT available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the SD Card is mounted on the device.
     **/
    public  boolean isSdCardMounted() {
        String status = Environment.getExternalStorageState();

        if (status != null && status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }

        return false;
    }


    /**
     * Shows an alert dialog with the OK button. When the user presses OK button, the dialog
     * dismisses.
     **/
    public  void showAlertDialog(Context context, String title, String body) {
        showAlertDialog(context, title, body, null);
    }

    /**
     * Shows an alert dialog with OK button
     **/
    public  void showAlertDialog(Context context, String title, String body, DialogInterface.OnClickListener okListener) {

        if (okListener == null) {
            okListener = new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            };
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage(body).setPositiveButton(android.R.string.ok, okListener);
        builder.setCancelable(false);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        builder.show();
    }

    /**
     * Serializes the Bitmap to Base64
     *
     * @return Base64 string value of a {@linkplain Bitmap} passed in as a parameter
     * @throws NullPointerException If the parameter bitmap is null.
     **/
    public String toBase64(Bitmap bitmap) {

        if (bitmap == null) {
            throw new NullPointerException("Bitmap cannot be null");
        }

        String base64Bitmap = null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBitmap = stream.toByteArray();
        base64Bitmap = Base64.encodeToString(imageBitmap, Base64.DEFAULT);

        return base64Bitmap;
    }

    /**
     * Converts the passed in drawable to Bitmap representation
     *
     * @throws NullPointerException If the parameter drawable is null.
     **/
    public Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable == null) {
            throw new NullPointerException("Drawable to convert should NOT be null");
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        if (drawable.getIntrinsicWidth() <= 0 && drawable.getIntrinsicHeight() <= 0) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }



    /**
     * this method is used to get the date with month name
     *
     * @param completeDate
     * @return
     */
    public String getFullSimpleDate(String completeDate) {
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd MMMM, yyyy");
        Date date = null;
        try {
            date = originalFormat.parse(completeDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = targetFormat.format(date);
        return formattedDate;
    }


    /**
     * Gives the device independent constant which can be used for scaling images, manipulating view
     * sizes and changing dimension and display pixels etc.
     **/
    public  float getDensityMultiplier(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A int value to represent dp equivalent to px value
     */
    public  int getDip(int px, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px * scale + 0.5f);
    }




    public String getDeviceId(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    /**
     * Gets the version code of the application. For e.g. Maverick Meerkat or 2013050301
     **/
    public  int getApplicationVersionCode(Context ctx) {

        int versionCode = 0;

        try {
            versionCode = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionCode;
    }

    /**
     * Gets the version number of the Android OS For e.g. 2.3.4 or 4.smart_dubai.2
     **/
    public String getOsVersion() {
        return Build.VERSION.RELEASE;
    }


    /**
     * Shares an application over the social network like Facebook, Twitter etc.
     *
     * @param sharingMsg   Message to be pre-populated when the 3rd party app dialog opens up.
     * @param emailSubject Message that shows up as a subject while sharing through email.
     * @param title        Title of the sharing options prompt. For e.g. "Share via" or "Share using"
     **/
    public  void share(Context ctx, String sharingMsg, String emailSubject, String title) {

        if(sharingMsg!=null && !sharingMsg.equalsIgnoreCase("")) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);

            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, sharingMsg);
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);

            ctx.startActivity(Intent.createChooser(sharingIntent, title));
        }
        else {

            //.makeText(ctx, R.string.please_enter_valid_url,Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * Checks if the input parameter is a valid email.
     *
     * @param email
     * @return
     */
    public  boolean isValidEmail(String email) {

        if (email == null) {
            return false;
        }

        final String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Matcher matcher;
        Pattern pattern = Pattern.compile(emailPattern);

        matcher = pattern.matcher(email);

        if (matcher != null) {
            return matcher.matches();
        } else {
            return false;
        }
    }


    /**
     * Capitalizes each word in the string.
     * @param string
     * @return
     */
    public String capitalizeString(String string) {

        if (string == null) {
            return null;
        }

        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You
                // can add other
                // chars here
                found = false;
            }
        } // end for

        return String.valueOf(chars);
    }



    /**
     * Get the file path from the Media Content Uri for video, audio or images.
     *
     * @param mediaContentUri Media content Uri.
     **/
    public String getPathForMediaUri(Context context, Uri mediaContentUri) {

        Cursor cur = null;
        String path = null;

        try {
            String[] projection = {MediaColumns.DATA};
            cur = context.getContentResolver().query(mediaContentUri, projection, null, null, null);

            if (cur != null && cur.getCount() != 0) {
                cur.moveToFirst();
                path = cur.getString(cur.getColumnIndexOrThrow(MediaColumns.DATA));
            }

            // Log.v( TAG, "#getRealPathFromURI Path: " + path );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cur != null && !cur.isClosed())
                cur.close();
        }

        return path;
    }

    public ArrayList<String> toStringArray(JSONArray jsonArr) {

        if (jsonArr == null || jsonArr.length() == 0) {
            return null;
        }

        ArrayList<String> stringArray = new ArrayList<String>();

        for (int i = 0, count = jsonArr.length(); i < count; i++) {
            try {
                String str = jsonArr.getString(i);
                stringArray.add(str);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return stringArray;
    }

    /**
     * Convert a given list of {@link String} into a {@link JSONArray}
     **/
    public JSONArray toJSONArray(ArrayList<String> stringArr) {
        JSONArray jsonArr = new JSONArray();

        for (int i = 0; i < stringArr.size(); i++) {
            String value = stringArr.get(i);
            jsonArr.put(value);
        }

        return jsonArr;
    }

    /**
     * Gets the data storage directory(pictures dir) for the device. If the external storage is not
     * available, this returns the reserved application data storage directory. SD Card storage will
     * be preferred over internal storage.
     *
     * @param dirName if the directory name is specified, it is created inside the DIRECTORY_PICTURES
     *                directory.
     * @return data storage directory on the device. Maybe be a directory on SD Card or internal
     * storage of the device.
     **/
    public File getStorageDirectory(Context ctx, String dirName) {

        if (TextUtils.isEmpty(dirName)) {
            dirName = "atemp";
        }

        File f = null;

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + dirName);
        } else {
            // media is removed, unmounted etc
            // Store image in
            // /data/data/<package-name>/cache/atemp/photograph.jpeg
            f = new File(ctx.getCacheDir() + "/" + dirName);
        }

        if (!f.exists()) {
            f.mkdirs();
        }

        return f;
    }

    /**
     * Given a file name, this method creates a {@link File} on best chosen device storage and
     * returns the file object. You can get the file path using {@link File#getAbsolutePath()}
     **/
    public File getFile(Context ctx, String fileName) {
        File dir = getStorageDirectory(ctx, null);
        File f = new File(dir, fileName);
        return f;
    }

    /**
     * @return Path of the image file that has been written.
     * @deprecated Use {@link(Context, byte[])}
     * Writes the given image to the external storage of the device. If external storage is not
     * available, the image is written to the application private directory
     **/
    public String writeImage(Context ctx, byte[] imageData) {

        final String FILE_NAME = "photograph.jpeg";
        File dir = null;
        String filePath = null;
        OutputStream imageFileOS;

        dir = getStorageDirectory(ctx, null);
        File f = new File(dir, FILE_NAME);

        try {
            imageFileOS = new FileOutputStream(f);
            imageFileOS.write(imageData);
            imageFileOS.flush();
            imageFileOS.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        filePath = f.getAbsolutePath();

        return filePath;
    }

    /**
     * Inserts an image into {@link Media} content provider of the device.
     *
     * @return The media content Uri to the newly created image, or null if the image failed to be
     * stored for any reason.
     **/
    public String writeImageToMedia(Context ctx, Bitmap image, String title, String description) {
        // TODO: move to MediaUtils
        if (ctx == null) {
            throw new NullPointerException("Context cannot be null");
        }

        return Media.insertImage(ctx.getContentResolver(), image, title, description);
    }



    /**
     * Returns the URL without the query string
     **/
    public URL getPathFromUrl(URL url) {

        if (url != null) {
            String urlStr = url.toString();
            String urlWithoutQueryString = urlStr.split("\\?")[0];
            try {
                return new URL(urlWithoutQueryString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Transforms Calendar to ISO 8601 string.
     **/
    public String fromCalendar(final Calendar calendar) {
        // TODO: move this method to DateUtils
        Date date = calendar.getTime();
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date);
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }

    /**
     * Gets current date and time formatted as ISO 8601 string.
     **/
    public String now() {
        // TODO: move this method to DateUtils
        return fromCalendar(GregorianCalendar.getInstance());
    }

    /**
     * Transforms ISO 8601 string to Calendar.
     **/
    public Calendar toCalendar(final String iso8601string) throws ParseException {
        // TODO: move this method to DateUtils
        Calendar calendar = GregorianCalendar.getInstance();
        String s = iso8601string.replace("Z", "+00:00");
        try {
            s = s.substring(0, 22) + s.substring(23);
        } catch (IndexOutOfBoundsException e) {
            // throw new org.apache.http.ParseException();
            e.printStackTrace();
        }

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        return calendar;
    }




    /**
     * Gets random color integer
     **/
    public  int getRandomColor() {
        Random random = new Random();
        int red = random.nextInt(255);
        int green = random.nextInt(255);
        int blue = random.nextInt(255);

        return Color.argb(255, red, green, blue);
    }

    /**
     * Converts a given bitmap to byte array
     */
    public  byte[] toBytes(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Resizes an image to the given width and height parameters Prefer using
     *
     * @param sourceBitmap Bitmap to be resized
     * @param newWidth     Width of resized bitmap
     * @param newHeight    Height of the resized bitmap
     */
    public Bitmap resizeImage(Bitmap sourceBitmap, int newWidth, int newHeight, boolean filter) {
        // TODO: move this method to ImageUtils
        if (sourceBitmap == null) {
            throw new NullPointerException("Bitmap to be resized cannot be null");
        }

        Bitmap resized = null;

        if (sourceBitmap.getWidth() < sourceBitmap.getHeight()) {
            // image is portrait
            resized = Bitmap.createScaledBitmap(sourceBitmap, newHeight, newWidth, true);
        } else {
            // image is landscape
            resized = Bitmap.createScaledBitmap(sourceBitmap, newWidth, newHeight, true);
        }

        resized = Bitmap.createScaledBitmap(sourceBitmap, newWidth, newHeight, true);

        return resized;
    }

    /**
     * <br/>
     * <br/>
     *
     * @param compressionFactor Powers of 2 are often faster/easier for the decoder to honor
     */
    public Bitmap compressImage(Bitmap sourceBitmap, int compressionFactor) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Config.ARGB_8888;
        opts.inSampleSize = compressionFactor;

        if (Build.VERSION.SDK_INT >= 10) {
            opts.inPreferQualityOverSpeed = true;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, opts);

        return image;
    }

    private  int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * Provide the height to which the sourceImage is to be resized. This method will calculate the
     * resultant height. Use scaleDownBitmap from {@link } wherever possible
     */
    public Bitmap resizeImageByHeight(int height, Bitmap sourceImage) {
        // TODO: move this method to ImageUtils
        int widthO = 0; // original width
        int heightO = 0; // original height
        int widthNew = 0;
        int heightNew = 0;

        widthO = sourceImage.getWidth();
        heightO = sourceImage.getHeight();
        heightNew = height;

        // Maintain the aspect ratio
        // of the original banner image.
        widthNew = (heightNew * widthO) / heightO;

        return Bitmap.createScaledBitmap(sourceImage, widthNew, heightNew, true);
    }

    /**
     * Checks if the url is valid
     */
    public  boolean isValidURL(String url) {
        URL urlObj;

        try {
            urlObj = new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }

        try {
            urlObj.toURI();
        } catch (URISyntaxException e) {
            return false;
        }

        return true;
    }



    /**
     * @param mimeType standard MIME type of the data.
     * @deprecated {@link #( String )}
     * Identifies if the content represented by the parameter mimeType is media. Image, Audio and
     * Video is treated as media by this method. You can refer to standard MIME type here. <a
     * href="http://www.iana.org/assignments/media-types/media-types.xhtml" >Standard MIME
     * types.</a>
     */
    public  boolean isMedia(String mimeType) {
        boolean isMedia = false;

        if (mimeType != null) {
            if (mimeType.startsWith("image/") || mimeType.startsWith("video/") || mimeType.startsWith("audio/")) {
                isMedia = true;
            }
        } else {
            isMedia = false;
        }

        return isMedia;
    }

    /**
     * Gets the Uri without the fragment. For e.g if the uri is
     * content://com.android.storage/data/images/48829#is_png the part after '#' is called as
     * fragment. This method strips the fragment and returns the url.
     */
    public String removeUriFragment(String url) {

        if (url == null || url.length() == 0) {
            return null;
        }

        String[] arr = url.split("#");

        if (arr.length == 2) {
            return arr[0];
        } else {
            return url;
        }
    }

    /**
     * Removes the parameters from the query from the uri
     */
    public String removeQueryParameters(Uri uri) {
        assert (uri.getAuthority() != null);
        assert (uri.getPath() != null);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(uri.getScheme());
        builder.encodedAuthority(uri.getAuthority());
        builder.encodedPath(uri.getPath());
        return builder.build().toString();
    }

    /**
     * Returns true if the mime type is a standard image mime type
     */
    public  boolean isImage(String mimeType) {
        if (mimeType != null) {
            if (mimeType.startsWith("image/")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }



    /**
     * Creates the uri to a file located on external storage or application internal storage.
     */
    public Uri createUri(Context ctx) {
        File root = getStorageDirectory(ctx, null);
        root.mkdirs();
        File file = new File(root, Long.toString(new Date().getTime()));
        Uri uri = Uri.fromFile(file);

        return uri;
    }


    /**
     * @deprecated Use {@link #( Context )}
     * Creates external content:// scheme uri to save the images at. The image saved at this
     * {@link Uri} will be visible via the gallery application on the device.
     */
    public Uri createImageUri(Context ctx) throws IOException {

        if (ctx == null) {
            throw new NullPointerException("Context cannot be null");
        }

        Uri imageUri = null;

        ContentValues values = new ContentValues();
        values.put(MediaColumns.TITLE, "");
        values.put(ImageColumns.DESCRIPTION, "");
        imageUri = ctx.getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);

        return imageUri;
    }



    public Bitmap roundBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        } else {
            sbmp = bmp;
        }

        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f, sbmp.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }




    /**
     * Checks if the parameter {@link Uri} is a content uri.
     **/
    public  boolean isContentUri(Uri uri) {
        if (!uri.toString().contains("content://")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Hides the already popped up keyboard from the screen.
     *
     * @param context
     */
    public  void hideKeyboard(Context context) {
        try {
            // use application level context to avoid unnecessary leaks.
            InputMethodManager inputManager = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            Log.e(TAG, "Sigh, cant even hide keyboard " + e.getMessage());
        }
    }

    /**
     * Checks if the build version passed as the parameter is
     * lower than the current build version.
     *
     * @param buildVersion One of the values from {@link Build.VERSION_CODES}
     * @return
     */
    public  boolean isBuildBelow(int buildVersion) {
        if (Build.VERSION.SDK_INT < buildVersion) return true;
        else return false;
    }

    /**
     * Sets the two parameter values to the parameter {@link TextView}
     * in null-safe and empty-safe way. Such method can be used when setting firstname-lastname
     * to a textview in the UI.
     *
     * @param textView
     * @param firstValue  String "null" will be treated as null value.
     * @param secondValue String "null" will be treated as null value.
     */
    public  void setTextValues(TextView textView, String firstValue, String secondValue) {
        String nullEmptyCheckedVal = getNullEmptyCheckedValue(firstValue, secondValue, null);
        textView.setText(nullEmptyCheckedVal);
    }


    /**
     * Returns concatenated values of atleast to or more strings provided to it
     * in a null safe manner.
     * @param firstValue
     * @param secondValue
     * @param delimiter Delimiter to be used to concatnate the parameter strings. If null, space characer will be used.
     * @param moreValues Optional
     * @return
     */
    public String getNullEmptyCheckedValue(String firstValue, String secondValue,
                                           String delimiter, String... moreValues) {
        if (TextUtils.isEmpty(delimiter)) {
            delimiter = " ";
        }

        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(firstValue) && !firstValue.equalsIgnoreCase("null")
                && secondValue != null && !secondValue.equalsIgnoreCase("null")) {
            builder.append(firstValue);
            builder.append(delimiter);
            builder.append(secondValue);
        } else if (!TextUtils.isEmpty(firstValue) && !firstValue.equalsIgnoreCase("null")) {
            builder.append(firstValue);
        } else if (!TextUtils.isEmpty(secondValue) && !secondValue.equalsIgnoreCase("null")) {
            builder.append(secondValue);
        }

        if (moreValues != null) {
            for (String value : moreValues) {
                if (!TextUtils.isEmpty(value) && !value.equalsIgnoreCase("null")) {
                    builder.append(delimiter);
                    builder.append(value);
                }
            }
        }

        return builder.toString();
    }





    /**
     * Method to generate the temp uri for the audio file
     *
     * @return uri
     */
    public Uri genarateAudio() {
        Uri audioFileUri = null;
        File outDir = getAudioDirectory();
        if (outDir != null) {
            String audioFileName = "/AUDIO_" + System.currentTimeMillis() + ".mp3";
            File outputFile = new File(outDir, audioFileName);
            audioFileUri = Uri.fromFile(outputFile);
        }
        return audioFileUri;
    }

    /**
     * Method to obtain the target directory
     @return directory
     */
    public File getAudioDirectory() {
        File outputDir = null;
        String externalStorageState = Environment.getExternalStorageState();
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            File audioDir = Environment.getExternalStorageDirectory();
            outputDir = new File(audioDir, "healthGroup" + "/" + "audio");
            if (!outputDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                outputDir.mkdirs();
            }
        }
        return outputDir;
    }







    /**
     * Partially capitalizes the string from paramter start and offset.
     *
     * @param string String to be formatted
     * @param start  Starting position of the substring to be capitalized
     * @param offset Offset of characters to be capitalized
     * @return
     */
    public String capitalizeString(String string, int start, int offset) {
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        String formattedString = string.substring(start, offset).toUpperCase() + string.substring(offset, string.length());
        return formattedString;
    }



    /*
    * method to save data in the app data android...
     */
    public File getAppDirectory(Context context) {
        File directory = new File(context.getFilesDir(), "/HealthGroup");
        if (!directory.exists()) {
            directory.mkdirs();
            FileOutputStream outputStream;
            try {
                outputStream = context.openFileOutput(directory.getName(), Context.MODE_PRIVATE);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return directory;
    }





    /**
     * Gets the extension of a file.
     */
    public String getExtension(File file) {
        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }

        return ext;
    }


    /**
     * show snackbar
     *
     * @param
     * @param
     */
    public void showSnackBar(View view, String message) {
        Snackbar snackbar;
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setAction("Action", null);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.parseColor("#ffffff"));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor("#045461"));
        snackbar.show();
    }










}