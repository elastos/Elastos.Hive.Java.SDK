package org.elastos.hive;

import java.io.FilenameFilter;

import org.elastos.hive.exceptions.HiveException;
import org.jetbrains.annotations.NotNull;

public abstract class HiveFile {
	/**
     * The serial version ID of class HiveFile.
     */
	@NotNull private final HiveDrive drive;

	protected HiveFile(HiveDrive drive) {
		this.drive = drive;
	}

	@NotNull
	protected HiveDrive getDrive() {
		return this.drive;
	}

	/**
	 * Create an file with specific pathname.
	 *
	 * @param drive The target drive to create a hive file.
	 * @param path  The pathname.
	 * @return An new hive file.
	 * @throws Exception TODO
	 */
	@NotNull
	public static HiveFile createFile(@NotNull HiveDrive drive,
			@NotNull String pathname) throws Exception {
		return drive.createFile(pathname);
	}

	/**
	 * Get absolute pathname of this item.
	 * @return	The pathname.
	 */
	@NotNull
	public abstract String getPath();

	/**
	 * Get parent pathname of this item.
	 * @return	The parent pathname.
	 */
	@NotNull
	public abstract String getParentPath();

	/**
	 * Get parent directory object.
	 * @return The parent directory object.
	 */
	@NotNull
	public abstract HiveFile getParent() throws HiveException;

	/**
	 * Get the created date and time of this item.
	 *
	 * @return The created date time.
	 */
	@NotNull
	public abstract String getCreatedTimeDate();

	/**
	 * Get the last date and time that updated this item.
	 *
	 * @return The last modified date time.
	 */
	@NotNull
	public abstract String getLastModifiedDateTime();

	/**
	 * Update date and time of this item.
	 *
	 * @param newDateTime The updated date and time.
	 * @throws Exception TODO
	 */
	@NotNull
	public abstract void updateDatetime(@NotNull String newDateTime) throws HiveException;

	/**
	 * Test if the item is a file.
	 *
	 * @return True if it's a file, otherwise false.
	 */
	public abstract boolean isFile();

	/**
	 * Test if the item is directory.
	 *
	 * @return True if it's a directory, otherwise false.
	 */
	public abstract boolean isDirectory();

	/**
	 * Copy the item to another address.
	 *
	 * @param newPath The copy-to pathname.
	 * @throws Exception TODO
	 */
	public abstract void copyTo(@NotNull String newPath) throws HiveException;

	/**
	 * Copy the item to another item.
	 *
	 * @param newFile The new Hive File object.
	 * @throws Exception TODO
	 */
	public abstract void copyTo(@NotNull HiveFile newFile) throws HiveException;

	/**
	 * Rename the item name to another name.
	 *
	 * @param newPath The new file path to rename with.
	 * @throws Exception TODO
	 */
	public abstract void renameTo(@NotNull String newPath) throws HiveException;

	/**
	 * Rename the item to new item.
	 *
	 * @param newFile The new Hive File to rename with.
	 * @throws Exception TODO
	 */
	public abstract void renameTo(@NotNull HiveFile newFile) throws HiveException;

	/**
	 * Delete this file object.
	 *
	 * @throws Exception TODO
	 */
	public abstract void delete() throws HiveException;

	/**
	 * List all file objects under this directory.
	 *
	 * @return The array of hive file objects.
	 * @throws Exception TODO
	 */
	@NotNull
	public abstract HiveFile[] list() throws HiveException;

	/**
	 * List all file objects under this directory.
	 *
	 * @param filter The filter to use.
	 * @return The array of hive file objects.
	 * @throws Exception TODO
	 */
	@NotNull
	public HiveFile[] list(@NotNull FilenameFilter filter) throws HiveException {
		HiveFile[] files = list();
		// TODO;
		return files;
	}

	/**
	 * List all file objects under this directory.
	 *
	 * @return The array of hive file objects.
	 * @throws Exception TODO
	 */
	@NotNull
	public HiveFile[] listFiles() throws HiveException {
		HiveFile[] files = list();
		// TODO;
		return files;
	}

	/**
	 * List all file objects under this directory.
	 *
	 * @param filter The filter to use
	 * @return The array of hive file objects
	 * @throws Exception TODO
	 */
	@NotNull
	public HiveFile[] listFiles(FilenameFilter filter) throws HiveException {
		HiveFile[] files = list();
		// TODO;
		return files;
	}

	/**
	 * Create a directory.
	 *
	 * @param pathname The new pathname to create
	 * @throws Exception TODO.
	 */
	public abstract void mkdir(@NotNull String pathname) throws HiveException;

	/**
	 * Create a directory along with all necessary parent directories.
	 *
	 * @param pathname The full pathname to create
	 * @throws Exception TODO
	 */
	public abstract void mkdirs(@NotNull String pathname) throws HiveException;

	/**
	 * Close hive file object.
	 *
	 * @throws Exception TODO
	 */
	public abstract void close() throws HiveException;

	@Override
	protected synchronized void finalize() {
		try {
			close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get string description.
	 */
	@Override
	public abstract String toString();
}