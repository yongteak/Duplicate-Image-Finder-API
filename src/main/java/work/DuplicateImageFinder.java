package work;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DuplicateImageFinder {
    public static void main(String[] args) throws IOException {
        // 로컬 이미지 폴더 경로
        List<List<String>> test = findDuplicateImagePairs("/Users/yongtaek/data/img/");
        System.out.println("Size : " + test.size());
        System.out.println(test);
    }

    /**
     * DHash 비교
     *
     * @param hashA
     * @param hashB
     * @param distance hash간 유사성 측정, 0에 가까울수록 동일한 이미지를 추출한다.
     * @return
     */
    private static boolean isSimilar(String hashA, String hashB, int distance) {
        if (hashA.length() != hashB.length()) {
            return false;
        }
        int diff = 0;
        for (int x = 0; x < hashA.length(); x++) {
            if (hashA.charAt(x) != hashB.charAt(x))
                diff++;
        }
        return diff <= distance;
    }

    /**
     * todo 라이브러리 교체?
     *
     * @param directoryPath
     * @return
     */
    private static List<File> getAllImagesFromDirectory(String directoryPath) {
        List<File> imagesPathList = null;
        String[] allowedExtensions = new String[]{"bmp", "gif", "jpeg", "jpg", "png", "psd", "pspimage", "thm", "tif"};
        File file = new File(directoryPath);
        if (file.isDirectory())
            imagesPathList = (List<File>) FileUtils.listFiles(file, allowedExtensions, true);
        return imagesPathList;
    }

    /**
     * 중복된 이미지 비교
     *
     * @param directoryPath
     * @return
     */
    private static List<List<String>> findDuplicateImagePairs(String directoryPath) throws IOException {
        // 파일 목록 조회
        List<File> imagesFileList = getAllImagesFromDirectory(directoryPath);
        if (null == imagesFileList || imagesFileList.isEmpty())
            return null;
        Map<String, String> imagesHashMap = new LinkedHashMap<String, String>();
        List<Map<String, String>> pairs = new ArrayList<Map<String, String>>();
        boolean isImageFoundInPair = false;
        String imageHash = null;
        for (File imagesFile : imagesFileList) {
            // 이미지 파일에서 해시 정보 추출
            imageHash = ImageUtility.getImageHash(imagesFile);
            if (imageHash != null && !imageHash.isEmpty()) {
                imagesHashMap.put(imagesFile.getAbsolutePath(), imageHash);
            }
        }

        for (Map.Entry<String, String> entry1 : imagesHashMap.entrySet())
            for (Map.Entry<String, String> entry2 : imagesHashMap.entrySet()) {
                if (!entry1.getKey().equalsIgnoreCase(entry2.getKey()) &&
                        isSimilar(entry1.getValue(), entry2.getValue(), 10)) {
                    // 중복된 이미지 파일 수집
                    isImageFoundInPair = false;
                    for (Map pair : pairs) {
                        if (pair.containsKey(entry1.getKey()) || pair.containsKey(entry2.getKey())) {
                            pair.put(entry1.getKey(), imagesHashMap.get(entry1.getKey()));
                            pair.put(entry2.getKey(), imagesHashMap.get(entry2.getKey()));
                            isImageFoundInPair = true;
                            break;
                        }
                    }
                    if (!isImageFoundInPair) {
                        Map<String, String> pair = new HashMap<String, String>();
                        pair.put(entry1.getKey(), imagesHashMap.get(entry1.getKey()));
                        pair.put(entry2.getKey(), imagesHashMap.get(entry2.getKey()));
                        pairs.add(pair);
                    }
                }
            }
        List<List<String>> findDuplicateImages = new ArrayList<List<String>>();
        if (null == pairs || pairs.isEmpty())
            return null;
        // 중복된 이미지 목록 k/v 생성
        for (Map<String, String> pair : pairs) {
            if (!pair.isEmpty() && pair.keySet().size() > 1) {
                List<String> item = new ArrayList<String>(pair.keySet());
                findDuplicateImages.add(item);
            }
        }
        return findDuplicateImages;
    }
}
