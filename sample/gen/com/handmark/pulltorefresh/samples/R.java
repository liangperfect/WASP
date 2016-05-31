/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * aapt tool from the resource data it found.  It
 * should not be modified by hand.
 */

package com.handmark.pulltorefresh.samples;

public final class R {
    public static final class anim {
        public static final int slide_in_from_bottom=0x7f040000;
        public static final int slide_in_from_top=0x7f040001;
        public static final int slide_out_to_bottom=0x7f040002;
        public static final int slide_out_to_top=0x7f040003;
    }
    public static final class attr {
        /**  BELOW HERE ARE DEPRECEATED. DO NOT USE. 
         <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
         */
        public static final int ptrAdapterViewBackground=0x7f010010;
        /**  Style of Animation should be used displayed when pulling. 
         <p>Must be one or more (separated by '|') of the following constant values.</p>
<table>
<colgroup align="left" />
<colgroup align="left" />
<colgroup align="left" />
<tr><th>Constant</th><th>Value</th><th>Description</th></tr>
<tr><td><code>rotate</code></td><td>0x0</td><td></td></tr>
<tr><td><code>flip</code></td><td>0x1</td><td></td></tr>
</table>
         */
        public static final int ptrAnimationStyle=0x7f01000c;
        /**  Drawable to use as Loading Indicator. Changes both Header and Footer. 
         <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
         */
        public static final int ptrDrawable=0x7f010006;
        /** <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
         */
        public static final int ptrDrawableBottom=0x7f010012;
        /**  Drawable to use as Loading Indicator in the Footer View. Overrides value set in ptrDrawable. 
         <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
         */
        public static final int ptrDrawableEnd=0x7f010008;
        /**  Drawable to use as Loading Indicator in the Header View. Overrides value set in ptrDrawable. 
         <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
         */
        public static final int ptrDrawableStart=0x7f010007;
        /** <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
         */
        public static final int ptrDrawableTop=0x7f010011;
        /**  A drawable to use as the background of the Header and Footer Loading Views 
         <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
         */
        public static final int ptrHeaderBackground=0x7f010001;
        /**  Text Color of the Header and Footer Loading Views Sub Header 
         <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
         */
        public static final int ptrHeaderSubTextColor=0x7f010003;
        /**  Base text color, typeface, size, and style for Header and Footer Loading Views 
         <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
         */
        public static final int ptrHeaderTextAppearance=0x7f01000a;
        /**  Text Color of the Header and Footer Loading Views 
         <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
         */
        public static final int ptrHeaderTextColor=0x7f010002;
        /** 
        	Whether PullToRefreshListView has it's extras enabled. This allows the user to be 
        	able to scroll while refreshing, and behaves better. It acheives this by adding
        	Header and/or Footer Views to the ListView.
        
         <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a boolean value, either "<code>true</code>" or "<code>false</code>".
         */
        public static final int ptrListViewExtrasEnabled=0x7f01000e;
        /**  Mode of Pull-to-Refresh that should be used 
         <p>Must be one or more (separated by '|') of the following constant values.</p>
<table>
<colgroup align="left" />
<colgroup align="left" />
<colgroup align="left" />
<tr><th>Constant</th><th>Value</th><th>Description</th></tr>
<tr><td><code>disabled</code></td><td>0x0</td><td></td></tr>
<tr><td><code>pullFromStart</code></td><td>0x1</td><td></td></tr>
<tr><td><code>pullFromEnd</code></td><td>0x2</td><td></td></tr>
<tr><td><code>both</code></td><td>0x3</td><td></td></tr>
<tr><td><code>manualOnly</code></td><td>0x4</td><td></td></tr>
<tr><td><code>pullDownFromTop</code></td><td>0x1</td><td> These last two are depreacted </td></tr>
<tr><td><code>pullUpFromBottom</code></td><td>0x2</td><td></td></tr>
</table>
         */
        public static final int ptrMode=0x7f010004;
        /**  Whether Android's built-in Over Scroll should be utilised for Pull-to-Refresh. 
         <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a boolean value, either "<code>true</code>" or "<code>false</code>".
         */
        public static final int ptrOverScroll=0x7f010009;
        /**  A drawable to use as the background of the Refreshable View 
         <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
         */
        public static final int ptrRefreshableViewBackground=0x7f010000;
        /** 
        	Whether the Drawable should be continually rotated as you pull. This only
        	takes effect when using the 'Rotate' Animation Style.
        
         <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a boolean value, either "<code>true</code>" or "<code>false</code>".
         */
        public static final int ptrRotateDrawableWhilePulling=0x7f01000f;
        /**  Whether the user can scroll while the View is Refreshing 
         <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a boolean value, either "<code>true</code>" or "<code>false</code>".
         */
        public static final int ptrScrollingWhileRefreshingEnabled=0x7f01000d;
        /**  Whether the Indicator overlay(s) should be used 
         <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a boolean value, either "<code>true</code>" or "<code>false</code>".
         */
        public static final int ptrShowIndicator=0x7f010005;
        /**  Base text color, typeface, size, and style for Header and Footer Loading Views Sub Header 
         <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
         */
        public static final int ptrSubHeaderTextAppearance=0x7f01000b;
    }
    public static final class dimen {
        public static final int header_footer_left_right_padding=0x7f070003;
        public static final int header_footer_top_bottom_padding=0x7f070004;
        public static final int indicator_corner_radius=0x7f070001;
        public static final int indicator_internal_padding=0x7f070002;
        public static final int indicator_right_padding=0x7f070000;
    }
    public static final class drawable {
        public static final int android=0x7f020000;
        public static final int bg_image=0x7f020001;
        public static final int default_ptr_flip=0x7f020002;
        public static final int default_ptr_rotate=0x7f020003;
        public static final int icon=0x7f020004;
        public static final int indicator_arrow=0x7f020005;
        public static final int indicator_bg_bottom=0x7f020006;
        public static final int indicator_bg_top=0x7f020007;
        public static final int wallpaper=0x7f020008;
    }
    public static final class id {
        public static final int both=0x7f060003;
        public static final int disabled=0x7f060000;
        public static final int fl_inner=0x7f060018;
        public static final int flip=0x7f060008;
        public static final int frag_ptr_list=0x7f060011;
        public static final int gridview=0x7f060009;
        public static final int manualOnly=0x7f060004;
        public static final int more_progress=0x7f060017;
        public static final int pullDownFromTop=0x7f060005;
        public static final int pullFromEnd=0x7f060002;
        public static final int pullFromStart=0x7f060001;
        public static final int pullUpFromBottom=0x7f060006;
        public static final int pull_refresh_expandable_list=0x7f06000d;
        public static final int pull_refresh_grid=0x7f06000e;
        public static final int pull_refresh_horizontalscrollview=0x7f06000f;
        public static final int pull_refresh_list=0x7f060010;
        public static final int pull_refresh_scrollview=0x7f060013;
        public static final int pull_refresh_viewpager=0x7f060014;
        public static final int pull_refresh_webview=0x7f060015;
        public static final int pull_refresh_webview2=0x7f060016;
        public static final int pull_to_refresh_image=0x7f060019;
        public static final int pull_to_refresh_progress=0x7f06001a;
        public static final int pull_to_refresh_sub_text=0x7f06001c;
        public static final int pull_to_refresh_text=0x7f06001b;
        public static final int rotate=0x7f060007;
        public static final int scrollview=0x7f06000b;
        public static final int viewpager=0x7f06000c;
        public static final int vp_list=0x7f060012;
        public static final int webview=0x7f06000a;
    }
    public static final class layout {
        public static final int activity_ptr_expandable_list=0x7f030000;
        public static final int activity_ptr_grid=0x7f030001;
        public static final int activity_ptr_horizontalscrollview=0x7f030002;
        public static final int activity_ptr_list=0x7f030003;
        public static final int activity_ptr_list_fragment=0x7f030004;
        public static final int activity_ptr_list_in_vp=0x7f030005;
        public static final int activity_ptr_scrollview=0x7f030006;
        public static final int activity_ptr_viewpager=0x7f030007;
        public static final int activity_ptr_webview=0x7f030008;
        public static final int activity_ptr_webview2=0x7f030009;
        public static final int layout_listview_in_viewpager=0x7f03000a;
        public static final int load_more=0x7f03000b;
        public static final int need_this_for_maven=0x7f03000c;
        public static final int pull_to_refresh_header_horizontal=0x7f03000d;
        public static final int pull_to_refresh_header_vertical=0x7f03000e;
    }
    public static final class raw {
        public static final int pull_event=0x7f050000;
        public static final int refreshing_sound=0x7f050001;
        public static final int reset_sound=0x7f050002;
    }
    public static final class string {
        public static final int app_name=0x7f080007;
        public static final int filler_text=0x7f080008;
        public static final int hello=0x7f080006;
        /**  Just use standard Pull Down String when pulling up. These can be set for languages which require it 
 Just use standard Pull Down String when pulling up. These can be set for languages which require it 
         */
        public static final int pull_to_refresh_from_bottom_pull_label=0x7f080003;
        public static final int pull_to_refresh_from_bottom_refreshing_label=0x7f080005;
        public static final int pull_to_refresh_from_bottom_release_label=0x7f080004;
        public static final int pull_to_refresh_pull_label=0x7f080000;
        public static final int pull_to_refresh_refreshing_label=0x7f080002;
        public static final int pull_to_refresh_release_label=0x7f080001;
    }
    public static final class style {
        public static final int HorizScrollViewItem=0x7f090000;
    }
    public static final class styleable {
        /** Attributes that can be used with a PullToRefresh.
           <p>Includes the following attributes:</p>
           <table>
           <colgroup align="left" />
           <colgroup align="left" />
           <tr><th>Attribute</th><th>Description</th></tr>
           <tr><td><code>{@link #PullToRefresh_ptrAdapterViewBackground com.handmark.pulltorefresh.samples:ptrAdapterViewBackground}</code></td><td> BELOW HERE ARE DEPRECEATED.</td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrAnimationStyle com.handmark.pulltorefresh.samples:ptrAnimationStyle}</code></td><td> Style of Animation should be used displayed when pulling.</td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrDrawable com.handmark.pulltorefresh.samples:ptrDrawable}</code></td><td> Drawable to use as Loading Indicator.</td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrDrawableBottom com.handmark.pulltorefresh.samples:ptrDrawableBottom}</code></td><td></td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrDrawableEnd com.handmark.pulltorefresh.samples:ptrDrawableEnd}</code></td><td> Drawable to use as Loading Indicator in the Footer View.</td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrDrawableStart com.handmark.pulltorefresh.samples:ptrDrawableStart}</code></td><td> Drawable to use as Loading Indicator in the Header View.</td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrDrawableTop com.handmark.pulltorefresh.samples:ptrDrawableTop}</code></td><td></td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrHeaderBackground com.handmark.pulltorefresh.samples:ptrHeaderBackground}</code></td><td> A drawable to use as the background of the Header and Footer Loading Views </td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrHeaderSubTextColor com.handmark.pulltorefresh.samples:ptrHeaderSubTextColor}</code></td><td> Text Color of the Header and Footer Loading Views Sub Header </td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrHeaderTextAppearance com.handmark.pulltorefresh.samples:ptrHeaderTextAppearance}</code></td><td> Base text color, typeface, size, and style for Header and Footer Loading Views </td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrHeaderTextColor com.handmark.pulltorefresh.samples:ptrHeaderTextColor}</code></td><td> Text Color of the Header and Footer Loading Views </td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrListViewExtrasEnabled com.handmark.pulltorefresh.samples:ptrListViewExtrasEnabled}</code></td><td>
        	Whether PullToRefreshListView has it's extras enabled.</td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrMode com.handmark.pulltorefresh.samples:ptrMode}</code></td><td> Mode of Pull-to-Refresh that should be used </td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrOverScroll com.handmark.pulltorefresh.samples:ptrOverScroll}</code></td><td> Whether Android's built-in Over Scroll should be utilised for Pull-to-Refresh.</td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrRefreshableViewBackground com.handmark.pulltorefresh.samples:ptrRefreshableViewBackground}</code></td><td> A drawable to use as the background of the Refreshable View </td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrRotateDrawableWhilePulling com.handmark.pulltorefresh.samples:ptrRotateDrawableWhilePulling}</code></td><td>
        	Whether the Drawable should be continually rotated as you pull.</td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrScrollingWhileRefreshingEnabled com.handmark.pulltorefresh.samples:ptrScrollingWhileRefreshingEnabled}</code></td><td> Whether the user can scroll while the View is Refreshing </td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrShowIndicator com.handmark.pulltorefresh.samples:ptrShowIndicator}</code></td><td> Whether the Indicator overlay(s) should be used </td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrSubHeaderTextAppearance com.handmark.pulltorefresh.samples:ptrSubHeaderTextAppearance}</code></td><td> Base text color, typeface, size, and style for Header and Footer Loading Views Sub Header </td></tr>
           </table>
           @see #PullToRefresh_ptrAdapterViewBackground
           @see #PullToRefresh_ptrAnimationStyle
           @see #PullToRefresh_ptrDrawable
           @see #PullToRefresh_ptrDrawableBottom
           @see #PullToRefresh_ptrDrawableEnd
           @see #PullToRefresh_ptrDrawableStart
           @see #PullToRefresh_ptrDrawableTop
           @see #PullToRefresh_ptrHeaderBackground
           @see #PullToRefresh_ptrHeaderSubTextColor
           @see #PullToRefresh_ptrHeaderTextAppearance
           @see #PullToRefresh_ptrHeaderTextColor
           @see #PullToRefresh_ptrListViewExtrasEnabled
           @see #PullToRefresh_ptrMode
           @see #PullToRefresh_ptrOverScroll
           @see #PullToRefresh_ptrRefreshableViewBackground
           @see #PullToRefresh_ptrRotateDrawableWhilePulling
           @see #PullToRefresh_ptrScrollingWhileRefreshingEnabled
           @see #PullToRefresh_ptrShowIndicator
           @see #PullToRefresh_ptrSubHeaderTextAppearance
         */
        public static final int[] PullToRefresh = {
            0x7f010000, 0x7f010001, 0x7f010002, 0x7f010003,
            0x7f010004, 0x7f010005, 0x7f010006, 0x7f010007,
            0x7f010008, 0x7f010009, 0x7f01000a, 0x7f01000b,
            0x7f01000c, 0x7f01000d, 0x7f01000e, 0x7f01000f,
            0x7f010010, 0x7f010011, 0x7f010012
        };
        /**
          <p>
          @attr description
           BELOW HERE ARE DEPRECEATED. DO NOT USE. 


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
          <p>This is a private symbol.
          @attr name android:ptrAdapterViewBackground
        */
        public static final int PullToRefresh_ptrAdapterViewBackground = 16;
        /**
          <p>
          @attr description
           Style of Animation should be used displayed when pulling. 


          <p>Must be one or more (separated by '|') of the following constant values.</p>
<table>
<colgroup align="left" />
<colgroup align="left" />
<colgroup align="left" />
<tr><th>Constant</th><th>Value</th><th>Description</th></tr>
<tr><td><code>rotate</code></td><td>0x0</td><td></td></tr>
<tr><td><code>flip</code></td><td>0x1</td><td></td></tr>
</table>
          <p>This is a private symbol.
          @attr name android:ptrAnimationStyle
        */
        public static final int PullToRefresh_ptrAnimationStyle = 12;
        /**
          <p>
          @attr description
           Drawable to use as Loading Indicator. Changes both Header and Footer. 


          <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
          <p>This is a private symbol.
          @attr name android:ptrDrawable
        */
        public static final int PullToRefresh_ptrDrawable = 6;
        /**
          <p>This symbol is the offset where the {@link com.handmark.pulltorefresh.samples.R.attr#ptrDrawableBottom}
          attribute's value can be found in the {@link #PullToRefresh} array.


          <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
          @attr name android:ptrDrawableBottom
        */
        public static final int PullToRefresh_ptrDrawableBottom = 18;
        /**
          <p>
          @attr description
           Drawable to use as Loading Indicator in the Footer View. Overrides value set in ptrDrawable. 


          <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
          <p>This is a private symbol.
          @attr name android:ptrDrawableEnd
        */
        public static final int PullToRefresh_ptrDrawableEnd = 8;
        /**
          <p>
          @attr description
           Drawable to use as Loading Indicator in the Header View. Overrides value set in ptrDrawable. 


          <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
          <p>This is a private symbol.
          @attr name android:ptrDrawableStart
        */
        public static final int PullToRefresh_ptrDrawableStart = 7;
        /**
          <p>This symbol is the offset where the {@link com.handmark.pulltorefresh.samples.R.attr#ptrDrawableTop}
          attribute's value can be found in the {@link #PullToRefresh} array.


          <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
          @attr name android:ptrDrawableTop
        */
        public static final int PullToRefresh_ptrDrawableTop = 17;
        /**
          <p>
          @attr description
           A drawable to use as the background of the Header and Footer Loading Views 


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
          <p>This is a private symbol.
          @attr name android:ptrHeaderBackground
        */
        public static final int PullToRefresh_ptrHeaderBackground = 1;
        /**
          <p>
          @attr description
           Text Color of the Header and Footer Loading Views Sub Header 


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
          <p>This is a private symbol.
          @attr name android:ptrHeaderSubTextColor
        */
        public static final int PullToRefresh_ptrHeaderSubTextColor = 3;
        /**
          <p>
          @attr description
           Base text color, typeface, size, and style for Header and Footer Loading Views 


          <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
          <p>This is a private symbol.
          @attr name android:ptrHeaderTextAppearance
        */
        public static final int PullToRefresh_ptrHeaderTextAppearance = 10;
        /**
          <p>
          @attr description
           Text Color of the Header and Footer Loading Views 


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
          <p>This is a private symbol.
          @attr name android:ptrHeaderTextColor
        */
        public static final int PullToRefresh_ptrHeaderTextColor = 2;
        /**
          <p>
          @attr description
          
        	Whether PullToRefreshListView has it's extras enabled. This allows the user to be 
        	able to scroll while refreshing, and behaves better. It acheives this by adding
        	Header and/or Footer Views to the ListView.
        


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a boolean value, either "<code>true</code>" or "<code>false</code>".
          <p>This is a private symbol.
          @attr name android:ptrListViewExtrasEnabled
        */
        public static final int PullToRefresh_ptrListViewExtrasEnabled = 14;
        /**
          <p>
          @attr description
           Mode of Pull-to-Refresh that should be used 


          <p>Must be one or more (separated by '|') of the following constant values.</p>
<table>
<colgroup align="left" />
<colgroup align="left" />
<colgroup align="left" />
<tr><th>Constant</th><th>Value</th><th>Description</th></tr>
<tr><td><code>disabled</code></td><td>0x0</td><td></td></tr>
<tr><td><code>pullFromStart</code></td><td>0x1</td><td></td></tr>
<tr><td><code>pullFromEnd</code></td><td>0x2</td><td></td></tr>
<tr><td><code>both</code></td><td>0x3</td><td></td></tr>
<tr><td><code>manualOnly</code></td><td>0x4</td><td></td></tr>
<tr><td><code>pullDownFromTop</code></td><td>0x1</td><td> These last two are depreacted </td></tr>
<tr><td><code>pullUpFromBottom</code></td><td>0x2</td><td></td></tr>
</table>
          <p>This is a private symbol.
          @attr name android:ptrMode
        */
        public static final int PullToRefresh_ptrMode = 4;
        /**
          <p>
          @attr description
           Whether Android's built-in Over Scroll should be utilised for Pull-to-Refresh. 


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a boolean value, either "<code>true</code>" or "<code>false</code>".
          <p>This is a private symbol.
          @attr name android:ptrOverScroll
        */
        public static final int PullToRefresh_ptrOverScroll = 9;
        /**
          <p>
          @attr description
           A drawable to use as the background of the Refreshable View 


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
          <p>This is a private symbol.
          @attr name android:ptrRefreshableViewBackground
        */
        public static final int PullToRefresh_ptrRefreshableViewBackground = 0;
        /**
          <p>
          @attr description
          
        	Whether the Drawable should be continually rotated as you pull. This only
        	takes effect when using the 'Rotate' Animation Style.
        


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a boolean value, either "<code>true</code>" or "<code>false</code>".
          <p>This is a private symbol.
          @attr name android:ptrRotateDrawableWhilePulling
        */
        public static final int PullToRefresh_ptrRotateDrawableWhilePulling = 15;
        /**
          <p>
          @attr description
           Whether the user can scroll while the View is Refreshing 


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a boolean value, either "<code>true</code>" or "<code>false</code>".
          <p>This is a private symbol.
          @attr name android:ptrScrollingWhileRefreshingEnabled
        */
        public static final int PullToRefresh_ptrScrollingWhileRefreshingEnabled = 13;
        /**
          <p>
          @attr description
           Whether the Indicator overlay(s) should be used 


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a boolean value, either "<code>true</code>" or "<code>false</code>".
          <p>This is a private symbol.
          @attr name android:ptrShowIndicator
        */
        public static final int PullToRefresh_ptrShowIndicator = 5;
        /**
          <p>
          @attr description
           Base text color, typeface, size, and style for Header and Footer Loading Views Sub Header 


          <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
          <p>This is a private symbol.
          @attr name android:ptrSubHeaderTextAppearance
        */
        public static final int PullToRefresh_ptrSubHeaderTextAppearance = 11;
    };
}