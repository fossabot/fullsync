/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *
 * For information about the authors of this project Have a look
 * at the AUTHORS file in the root of this project.
 */
package net.sourceforge.fullsync.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.buffer.EntryDescriptor;
import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FileCopyEntryDescriptor implements EntryDescriptor {
	private Object reference;
	private File src;
	private File dst;
	private InputStream inputStream;
	private OutputStream outputStream;

	public FileCopyEntryDescriptor(Object reference, File src, File dst) {
		this.reference = reference;
		this.src = src;
		this.dst = dst;
	}

	@Override
	public Object getReferenceObject() {
		return reference;
	}

	@Override
	public long getLength() {
		return src.getFileAttributes().getLength();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (inputStream == null) {
			inputStream = src.getInputStream();
		}
		return inputStream;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		if (outputStream == null) {
			outputStream = dst.getOutputStream();
		}
		return outputStream;
	}

	@Override
	public void finishWrite() {
		try {
			if (outputStream != null) {
				outputStream.close();
			}
			dst.setFileAttributes(src.getFileAttributes());
			dst.writeFileAttributes();
			dst.refresh();
		}
		catch (IOException ex) {
			ExceptionHandler.reportException(ex);
		}
	}

	@Override
	public void finishStore() {
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		catch (IOException ex) {
		}
	}

	@Override
	public String getOperationDescription() {
		return "Copied " + src.getPath() + " to " + dst.getPath();
	}
}
