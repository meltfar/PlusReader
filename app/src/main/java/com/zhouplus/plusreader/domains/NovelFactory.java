package com.zhouplus.plusreader.domains;

import android.graphics.Paint;
import android.support.annotation.Nullable;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Vector;

/**
 * Created by zhouplus
 * Time at 2016/9/6
 * Project name PlusReader
 * Description :
 * Author's email :
 * Version 1.0
 */
public class NovelFactory {
    MappedByteBuffer globalBuffer;//小说读取进内存
    public int mPageBeginPos, mPageEndPos;//当前页面开始和结束，在小说文本中的位置
    long fileLength;//小说整体长度
    int mGlobalWidth, mGlobalHeight;
    int mPageLineCount;//每一页能容纳的行数
    int mTextSize, mLineSpace;
    Paint mPaint;
    //所使用的字符串编码
    public String mEncoding = null;

    /**
     * 初始化函数，设置了可显示区域的宽和高，以及一个可选的编码格式
     *
     * @param w      宽
     * @param h      高
     * @param encode 解析文件所使用的编码，如果不指定，则自动解析
     */
    public NovelFactory(int w, int h, @Nullable String encode) {
        this.mGlobalWidth = w;
        this.mGlobalHeight = h;
        if (encode != null) {
            this.mEncoding = encode;
        }
    }

    private String analyzeEncoding(File f) {
        UniversalDetector ud = new UniversalDetector(null);
        byte[] b = new byte[4096];
        try {
            FileInputStream fis = new FileInputStream(f);
            int dwRead;
            while ((dwRead = fis.read(b)) > 0 && !ud.isDone()) {
                ud.handleData(b, 0, dwRead);
            }
            ud.dataEnd();
            fis.close();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        String encoding = ud.getDetectedCharset();
        ud.reset();
        if (encoding == null) {
            return "GBK";
        }
        return encoding;
    }

    /**
     * 获得文字大小和行间距，用于计算都需要读取多少行字符
     *
     * @param textSize  文字高度
     * @param lineSpace 行间距
     */
    public Vector<String> setArguments(int textSize, int lineSpace, Paint paint) {
        this.mTextSize = textSize;
        this.mLineSpace = lineSpace;
        this.mPaint = paint;
        this.mPageLineCount = mGlobalHeight / (mTextSize + mLineSpace);
        mPageEndPos = mPageBeginPos;
        return nextPage();
    }

    public Vector<String> refreshPage() {
        prePage();
        return nextPage();
    }

    public Vector<String> setPercent(float percent) {
        float a = (fileLength * percent) / 100;
        mPageEndPos = Math.round(a);
        if (mPageEndPos == 0) {
            return nextPage();
        } else {
            nextPage();
            prePage();
            return nextPage();
        }
    }

    public boolean openBook(PlusBook book) {
        String path = book.path;
        File f = new File(path);
        fileLength = book.length;

        try {
            globalBuffer = new RandomAccessFile(f, "r").getChannel()
                    .map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
        } catch (IOException e) {
            return false;
        }

        mPageBeginPos = book.read_begin;
        mPageEndPos = book.read_end;
        //解析文件编码
        if (mEncoding == null) {
            String encoding = analyzeEncoding(f);
            if (encoding == null) {
                mEncoding = "GBK";
            } else {
                mEncoding = encoding;
            }
        }

        return true;
    }


    /**
     * 向后读取一个段落
     *
     * @param m_mbBufPos 当前位置
     * @return 读取结果
     */
    private byte[] readParagraphForward(int m_mbBufPos) {
        byte b0/* , b1 */;
        int i = m_mbBufPos;// 当前位置
        while (i < fileLength) {
            b0 = globalBuffer.get(i++);
            // 如果遇到了换行，就停止
            if (b0 == 0x0a/* 换行 */) {
                break;
            }
        }
        // 一个段的长度
        int nParaSize = i - m_mbBufPos;
        // 截取出的字符集
        byte[] buf = new byte[nParaSize];
        // 取出需要的部分
        for (i = 0; i < nParaSize; i++) {
            buf[i] = globalBuffer.get(m_mbBufPos + i);
        }
        return buf;
    }

    /**
     * 逻辑类似上面
     *
     * @param m_mbBufBeginPos 当前位置
     * @return 读取结果
     */
    private byte[] readParagraphBack(int m_mbBufBeginPos) {
        byte b0/* , b1 */;
        int i = m_mbBufBeginPos - 1;
        while (i > 0) {
            b0 = globalBuffer.get(i);
            if (b0 == 0x0a && i != m_mbBufBeginPos - 1) {
                i++;
                break;
            }
            i--;
        }
        int nParaSize = m_mbBufBeginPos - i;
        byte[] buf = new byte[nParaSize];
        for (int j = 0; j < nParaSize; j++) {
            buf[j] = globalBuffer.get(i + j);
        }
        return buf;
    }

    public Vector<String> pageDown() {
        String strParagraph;
        // 要截取出的容器
        Vector<String> lines = new Vector<>();
        while ((lines.size() < mPageLineCount) && (mPageEndPos < fileLength)) {
            // 向后读取
            byte[] parabuffer = readParagraphForward(mPageEndPos);
            // 指针移动
            mPageEndPos += parabuffer.length;
            try {
                strParagraph = new String(parabuffer, mEncoding);
            } catch (UnsupportedEncodingException e) {
                return null;
            }
            strParagraph = strParagraph.replaceAll("\r\n", "  ");
            strParagraph = strParagraph.replaceAll("\n", " ");

            while (strParagraph.length() > 0) {
                int paintSize = mPaint.breakText(strParagraph, true, mGlobalWidth, null);
                lines.add(strParagraph.substring(0, paintSize));
                strParagraph = strParagraph.substring(paintSize);
                if (lines.size() >= mPageLineCount) {
                    break;
                }
            }
            // 如果读过头了，
            // 也就是说，如果已经读取了100行，可是80行就已经填满屏幕了，那么剩下的20行就要退回去
            if (strParagraph.length() != 0) {
                try {
                    mPageEndPos -= (strParagraph).getBytes(mEncoding).length;
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            }
        }
        return lines;
    }

    private void pageUp() {
        String strParagraph = "";
        Vector<String> lines = new Vector<>();
        // 循环添加文字
        while ((lines.size() < mPageLineCount)/* 如果当前文字的行数已经超过了最大行数就停止 */
                && (mPageBeginPos > 0)/* 如果已经读取完了，也停止 */) {
            Vector<String> paraLines = new Vector<>();
            // 往回读取一段
            byte[] parabuffer = readParagraphBack(mPageBeginPos);
            // 指针向回移动
            mPageBeginPos -= parabuffer.length;
            try {
                // 按照指定编码读取
                strParagraph = new String(parabuffer, mEncoding);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            // 替换换行符
            strParagraph = strParagraph.replaceAll("\r\n", "  ");
            strParagraph = strParagraph.replaceAll("\n", " ");

            // 开始一行行测量解析段中的文本
            while (strParagraph.length() > 0) {
                // 这个函数就是测量指定宽度的文字用的
                // 这里意味，截取屏幕显示一行，所需要的文本
                int paintSize = mPaint.breakText(strParagraph, true, mGlobalWidth, null);
                // 向段容器中加入读取到的这一行
                paraLines.add(strParagraph.substring(0, paintSize));
                // 刷新剩下的文本
                strParagraph = strParagraph.substring(paintSize);
            }
            // 都添加回总容器中，直接添加到头部(因为是从后往前读的)
            lines.addAll(0, paraLines);

            // 把读取超出的部分换回去
            while (lines.size() > mPageLineCount) {
                try {
                    mPageBeginPos += lines.get(0).getBytes(mEncoding).length;
                    lines.remove(0);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            mPageEndPos = mPageBeginPos;
        }
    }

    /**
     * 取得下一页的文字
     * 如果已经到了最后一页，就返回一个新的空Vector，如果出错，就返回Null
     */
    public Vector<String> nextPage() {
        // 如果已经到头了，就返回
        if (mPageEndPos >= fileLength) {
            return new Vector<>();
        } else {
            // 清空文字，重新读取
            mPageBeginPos = mPageEndPos;
            return pageDown();
        }
    }

    /**
     * 取得上一页文字
     * 如果已经到了最后一页，就返回一个新的空Vector，如果出错，就返回Null
     */
    public Vector<String> prePage() {
        // 如果已经到头了，就返回
        if (mPageBeginPos <= 0) {
            return new Vector<>();
        }
        pageUp();
        return pageDown();
    }

    /**
     * 获取当前阅读位置
     *
     * @return
     */
    public int[] getCurrentPosition() {
        return new int[]{mPageBeginPos, mPageEndPos};
    }
}
