package com.ll.projectLimC.domain.medicine.bookmark.service;

import com.ll.projectLimC.domain.medicine.bookmark.entity.Bookmark;
import com.ll.projectLimC.domain.medicine.bookmark.repository.BookmarkRepository;
import com.ll.projectLimC.domain.medicine.medicine.entity.Medicine;
import com.ll.projectLimC.domain.medicine.medicine.repository.MedicineRepository;
import com.ll.projectLimC.domain.user.entity.User;
import com.ll.projectLimC.domain.user.repository.UserRepository;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final MedicineRepository medicineRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggleMedicineBookmark(Long medicineId, Long userId){
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NO_EXIST_THAT_MEDICINE));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        Optional<Bookmark> existingBookmark = bookmarkRepository.findByUserIdAndMedicineId(userId, medicineId);

        if (existingBookmark.isPresent()) {
            bookmarkRepository.delete(existingBookmark.get());
            return false; // 북마크 취소됨
        } else {
            Bookmark bookmark = Bookmark.builder()
                    .user(user)
                    .medicine(medicine)
                    .build();
            bookmarkRepository.save(bookmark);
            return true; // 북마크 등록됨
        }
    }
}
