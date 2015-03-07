<?php
/**
 * The base configurations of the WordPress.
 *
 * This file has the following configurations: MySQL settings, Table Prefix,
 * Secret Keys, and ABSPATH. You can find more information by visiting
 * {@link http://codex.wordpress.org/Editing_wp-config.php Editing wp-config.php}
 * Codex page. You can get the MySQL settings from your web host.
 *
 * This file is used by the wp-config.php creation script during the
 * installation. You don't have to use the web site, you can just copy this file
 * to "wp-config.php" and fill in the values.
 *
 * @package WordPress
 */

// ** MySQL settings - You can get this info from your web host ** //
/** The name of the database for WordPress */
define('DB_NAME', 'wordpress');

/** MySQL database username */
define('DB_USER', 'wordpress');

/** MySQL database password */
define('DB_PASSWORD', 'chateau');

/** MySQL hostname */
define('DB_HOST', 'localhost');

/** Database Charset to use in creating database tables. */
define('DB_CHARSET', 'utf8');

/** The Database Collate type. Don't change this if in doubt. */
define('DB_COLLATE', '');

/**#@+
 * Authentication Unique Keys and Salts.
 *
 * Change these to different unique phrases!
 * You can generate these using the {@link https://api.wordpress.org/secret-key/1.1/salt/ WordPress.org secret-key service}
 * You can change these at any point in time to invalidate all existing cookies. This will force all users to have to log in again.
 *
 * @since 2.6.0
 */
define('AUTH_KEY',         '$U^.Y B<Ozex2iP5~Zfg?AqAuX3<,fc*$e7+|A^8U1 v&wy*(Nu vf@vBT:#n#JF');
define('SECURE_AUTH_KEY',  'N9NHV9CWlt`AEnw2:png!)&^I.]vVf4>o}b.jJZ148&qAqdtR/%w]%h2l <K^]hU');
define('LOGGED_IN_KEY',    '}g%..}7Q()BPE_8*7}c(gY}Y2^YVS_=:IR-fVE@kqG80?^SYW]}SNP#FifoDdB[u');
define('NONCE_KEY',        '.|#fC8dugV6.B^mrIz@C!Y5U#-u`[pqf&jpe`f+e?lQ~8g32kaR}ZLf]DmRT*M7>');
define('AUTH_SALT',        '[@4.!qyy#&y`m6WTIa$R#6?~WoNSZQ<3Up-s?I2|pp>%y^iu9;L~P8:2`EG@JHp?');
define('SECURE_AUTH_SALT', 'Ei.}D8D~`+?+~PJ#v7R7wxi`z[73At)pL)[8=Kf{VMM*tuO|Q;N/}!-X )ooj49L');
define('LOGGED_IN_SALT',   'G&qBF~o1t*tC]c%=_y5!>1{70FRxzMBlnJ31(A#hj44HdHY5$1dJs9e_j75K9|@)');
define('NONCE_SALT',       'dXZ}Bt84H>6ESp{^lI[nqlnp,OIE7od*Q4Y<LZR>F`@]Eq@&N*QShL{4y)tA?UZ5');

/**#@-*/

/**
 * WordPress Database Table prefix.
 *
 * You can have multiple installations in one database if you give each a unique
 * prefix. Only numbers, letters, and underscores please!
 */
$table_prefix  = 'log_wp_';

/**
 * For developers: WordPress debugging mode.
 *
 * Change this to true to enable the display of notices during development.
 * It is strongly recommended that plugin and theme developers use WP_DEBUG
 * in their development environments.
 */
define('WP_DEBUG', false);

/* That's all, stop editing! Happy blogging. */

/** Absolute path to the WordPress directory. */
if ( !defined('ABSPATH') )
	define('ABSPATH', dirname(__FILE__) . '/');

/** Sets up WordPress vars and included files. */
require_once(ABSPATH . 'wp-settings.php');
