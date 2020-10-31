package org.elastos.hive;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.files.FileInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Files {
    /**
     * Initiates an upload sequence by returning a Write or OutputStream object that can be
     * used to write small file chunks. After writing, flush() must be called
     * to actually send the data remotely.
     * @param path the path for the remote file
     * @param resultType Write or OutputStream class
     * @param <T> Write, OutputStream
     * @return the new CompletionStage, the result is the Writer or OutputStream interface for
     *              upload the file content if success; null otherwise
     * @throws HiveException
     */
    <T> CompletableFuture<T> upload(String path, Class<T> resultType);

    /**
     * Initiates a download sequence by returning a Reader or InputStream object that can
     * be used to read the downloaded file in chunks.
     *
     * @param path the path for the remote file
     * @param resultType Reader or InputStream class
     * @return the new CompletionStage, the result is the Reader or InputStream interface for
     *      read the file content if success; null otherwise
     */
    <T> CompletableFuture<T> download(String path, Class<T> resultType);

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
     * Moves (or renames) a file or a folder.
     *
     * @param src the path for the remote source file or folder
     * @param dest the path for the remote destination file or folder
     * @return the new CompletionStage, the result is true if the file or folder
     *         successfully moved; false otherwise
     */
    CompletableFuture<Boolean> move(String src, String dest);

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
     * Returns the SHA256 hash of the given file.
     *
     * @param path path for the remote file
     * @return the new CompletionStage, the result is the base64 hash string
     *         if the hash successfully calculated; null otherwise
     */
    CompletableFuture<String> hash(String path);

    /**
     * Returns the list of all files in a given folder.
     * @param path the path for the remote folder
     * @return the new CompletionStage, the result is List if success; null otherwise
     * @throws HiveException
     */
    CompletableFuture<List<FileInfo>> list(String path);

    /**
     * Information about the target file or folder.
     *
     * @param path the path for the remote file or folder
     * @return the new CompletionStage, the result is FileInfo
     *         if success; null otherwise
     */
    CompletableFuture<FileInfo> stat(String path);

}
