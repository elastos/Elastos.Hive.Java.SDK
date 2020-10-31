package org.elastos.hive.network.model;

import java.util.List;

import org.elastos.hive.file.FileInfo;

public class FilesResponse {
	private List<FileInfo> file_info_list;

	public List<FileInfo> getFiles() {
		return file_info_list;
	}
}
