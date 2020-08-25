package org.elastos.hive.interfaces;

import org.elastos.hive.Callback;
import org.elastos.hive.FileInfo;

import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Files {

    /**

     * Create a new folder.
     *
     * @param path the path for the remote folder
     * @return the new CompletionStage, the result is true if the folder
     *         successfully created; false otherwise
     */
    CompletableFuture<Boolean> createFolder(String path);
    /**

     * Create a new folder.
     *
     * @param path the path for the remote folder
     * @param callback the given Callback will be called once the operation
     *         completes, either success or not
     * @return the new CompletionStage, the result is true if the folder
     *         successfully created; false otherwise
     */
    CompletableFuture<Boolean> createFolder(String path, Callback<Boolean> callback);
    /**

     * Initiates an upload sequence by returning a Write object that can be
     * used to write small file chunks. After writing, flush() must be called
     * to actually send the data remotely.
     *
     * @param path the path for the remote file
     * @return the new CompletionStage, the result is the Writer interface for
     *      upload the file content if success; null otherwise
     */
    CompletableFuture<Writer> upload(String path);
    /**

     * Initiates an upload sequence by returning a Write object that can be
     * used to write small file chunks. After writing, flush() must be called
     * to actually send the data remotely.
     *
     * @param path the path for the remote file
     * @param callback the given Callback will be called once the operation
     *         completes, either Writer or null
     * @return the new CompletionStage, the result is the Writer interface for
     *      upload the file content if success; null otherwise
     */
    CompletableFuture<Writer> upload(String path, Callback<Writer> callback);
    /**

     * Initiates a download sequence by returning a Reader object that can
     * be used to read the downloaded file in chunks.
     *
     * @param path the path for the remote file
     * @return the new CompletionStage, the result is the Reader interface for
     *      read the file content if success; null otherwise
     */
    CompletableFuture<Reader> download(String path);
    /**

     * Initiates a download sequence by returning a Reader object that can
     * be used to read the downloaded file in chunks.
     *
     * @param path the path for the remote file
     * @param callback the given Callback will be called once the operation
     *         completes, either Reader or null
     * @return the new CompletionStage, the result is the Reader interface for
     *      read the file content if success; null otherwise
     */
    CompletableFuture<Reader> download(String path, Callback<Reader> callback);
    /**

     * Deletes a file, or a folder. In case the given path is a folder,
     * deletion is recursive.
     *
     * @param path the path for the remote file or folder
     * @return the new CompletionStage, the result is true if the file or folder
     *         successfully deleted; false otherwise
     */
    CompletableFuture<Boolean> delete(String path);
    /**

     * Deletes a file, or a folder. In case the given path is a folder,
     * deletion is recursive.
     *
     * @param path the path for the remote file or folder
     * @param callback the given Callback will be called once the operation
     *         completes, either success or not
     * @return the new CompletionStage, the result is true if the file or folder
     *         successfully deleted; false otherwise
     */
    CompletableFuture<Boolean> delete(String path, Callback<Boolean> callback);
    /**

     * Moves (or renames) a file or a folder.
     *
     * @param src the path for the remote source file or folder
     * @param dest the path for the remote destination file or folder
     * @return the new CompletionStage, the result is true if the file or folder
     *         successfully moved; false otherwise
     */
    CompletableFuture<Boolean> move(String src, String dest);
    /**

     * Moves (or renames) a file or a folder.
     *
     * @param src the path for the remote source file or folder
     * @param dest the path for the remote destination file or folder
     * @param callback the given Callback will be called once the operation
     *         completes, either success or not
     * @return the new CompletionStage, the result is true if the file or folder
     *         successfully moved; false otherwise
     */
    CompletableFuture<Boolean> move(String src, String dest, Callback<Boolean> callback);
    /**

     * Copies a file or a folder (recursively).
     *
     * @param src the path for the remote source file or folder
     * @param dest the path for the remote destination file or folder
     * @return the new CompletionStage, the result is true if the file or folder
     *         successfully copied; false otherwise
     */
    CompletableFuture<Boolean> copy(String src, String dest);
    /**

     * Copies a file or a folder (recursively).
     *
     * @param src the path for the remote source file or folder
     * @param dest the path for the remote destination file or folder
     * @param callback the given Callback will be called once the operation
     *         completes, either success or not
     * @return the new CompletionStage, the result is true if the file or folder
     *         successfully copied; false otherwise
     */
    CompletableFuture<Boolean> copy(String src, String dest, Callback<Boolean> callback);
    /**

     * Returns the SHA256 hash of the given file.
     *
     * @param path path for the remote file
     * @return the new CompletionStage, the result is the base64 hash string
     *         if the hash successfully calculated; null otherwise
     */
    CompletableFuture<String> hash(String path);
    /**

     * Returns the SHA256 hash of the given file.
     *
     * @param path the path for the remote file
     * @param callback the given Callback will be called once the operation
     *         completes, either hash or null
     * @return the new CompletionStage, the result is the base64 hash string
     *         if the hash successfully calculated; null otherwise
     */
    CompletableFuture<String> hash(String path, Callback<String> callback);
    /**

     * Returns the list of all files in a given folder.
     *
     * @param path the path for the remote folder
     * @return the new CompletionStage, the result is List<FileInfo>
     *         if success; null otherwise
     */
    CompletableFuture<List<FileInfo>> list(String path);
    /**

     * Returns the list of all files in a given folder.
     *
     * @param path the path for the remote folder
     * @param callback the given Callback will be called once the operation
     *         completes, either List<FileInfo> or null
     * @return the new CompletionStage, the result is List<FileInfo>
     *         if success; null otherwise
     */
    CompletableFuture<List<FileInfo>> list(String path, Callback<List<FileInfo>> callback);
    /**

     * Information about the target file or folder.
     *
     * @param path the path for the remote file or folder
     * @return the new CompletionStage, the result is FileInfo
     *         if success; null otherwise
     */
    CompletableFuture<FileInfo> stat(String path);
    /**

     * Information about the target file or folder.
     *
     * @param path the path for the remote file or folder
     * @param callback the given Callback will be called once the operation
     *         completes, either FileInfo or null
     * @return the new CompletionStage, the result is FileInfo
     *         if success; null otherwise
     */
    CompletableFuture<FileInfo> stat(String path, Callback<FileInfo> callback);
}
