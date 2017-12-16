package com.example.util;

import com.example.item.ItemAbout;
import com.example.item.ItemGIF;
import com.example.item.ItemPhotos;
import com.viaviapp.hdwallpaper.R;

import java.io.Serializable;
import java.util.ArrayList;

public class Constant implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//server url
	public static String SERVER_URL = "http://panel.alivemessages.com//";

	//uploaded image of server where image store
//	public static final String SERVER_IMAGE_UPFOLDER_CATEGORY=SERVER_URL + "/categories/";
//
//	//uploaded image thumb of server where image store
//	public static final String SERVER_IMAGE_UPFOLDER_THUMB=SERVER_URL + "/images/thumbs/";

	//latest wallpaper
	public static final String LATEST_URL = SERVER_URL + "api.php?latest";

	//category
	public static final String CATEGORY_URL = SERVER_URL + "api.php?cat_list";

	//this url gives item of specific category.
	public static final String CATEGORY_ITEM_URL = SERVER_URL + "/api.php?cat_id=";

	public static final String URL_ABOUT_US = SERVER_URL + "api.php";

	public static final String URL_WALLPAPER = SERVER_URL + "api.php?wallpaper_id=";
	public static final String URL_GIF = SERVER_URL + "api.php?gif_id=";
	public static final String URL_GIFs_lIST = SERVER_URL + "api.php?gif_list";
	public static final String URL_ABOUT_US_LOGO = SERVER_URL + "images/";

	public static final String TAG_ROOT="HD_WALLPAPER";
	public static final String TAG_CAT_ID="cid";
	public static final String TAG_CAT_NAME="category_name";
	public static final String TAG_CAT_IMAGE="category_image";
	public static final String TAG_CAT_IMAGE_THUMB="category_image_thumb";
	public static final String TAG_TOTAL_WALL="total_wallpaper";

	public static final String TAG_WALL_ID="id";
	public static final String TAG_WALL_IMAGE="wallpaper_image";
	public static final String TAG_WALL_IMAGE_THUMB="wallpaper_image_thumb";
	public static final String TAG_GIF_IMAGE="gif_image";

	public static final String TAG_WALL_VIEWS="total_views";

	public static String CATEGORY_TITLE;
	public static String CATEGORY_ID;
	
	//Wallpaper SD Card Download Path
	public static final String DOWNLOAD_SDCARD_FOLDER_PATH_WALLPAPER="/HDWallpaper/";

	// Number of columns of Grid View
	public static final int NUM_OF_COLUMNS = 2;

	// Gridview image padding
	public static final int GRID_PADDING = 3; // in dp

	public static ArrayList<ItemPhotos> arrayList = new ArrayList<ItemPhotos>();
	public static ArrayList<ItemGIF> arrayListGIF = new ArrayList<ItemGIF>();
	public static ItemAbout itemAbout;
	public static int columnWidth = 0;

	public static Boolean isToggle = true, isFav = false;
	public static int color = 0xff1E88E5;
	public static int theme = R.style.AppTheme_blue;
	public static int drawable = R.drawable.bg_nav_theme_blue;

	public static int adShow = 5;
	public static int adCount = 0;

}
