package com.mj.beko.repository;

import com.mj.beko.domain.OperatorShift;
import com.mj.beko.domain.OperatorShiftDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Ricardo on 2017/12/8.
 */
@Repository
@Transactional
public interface ShiftDetailRepository extends JpaRepository<OperatorShiftDetail,Long>,JpaSpecificationExecutor {

    @Query(value = "delete  from t_shift_details where operator_shift_id is null",nativeQuery = true)
    @Modifying
    void deleteNullShiftDetail();

    @Query(value = "select * from t_shift_details where operator_shift_id=:id",nativeQuery = true)
    List<OperatorShiftDetail> getOperatorShiftDetailByShiftId(@Param("id") String id);

    /**
     * 查询当前shift某个班次的工作时间和休息时间
     * @param id
     * @param contentType
     * @param name
     * @return
     */
    @Query(value = "select * from t_shift_details where operator_shift_id=:id and content_type=:contentType and name=:name",nativeQuery = true)
    List<OperatorShiftDetail> getOperatorShiftDetailByShiftIdAnAndContentType(@Param("id") String id,@Param("contentType") String contentType,@Param("name") String name);

    /**
     * 通过shiftId和Name 查询当前所在的shiftDetails,查找其中的开始时间和结束时间,standOutPut
     * @param shiftId
     * @param shiftName
     * @return
     */
    @Query(value = "select  * from t_shift_details where operator_shift_id=:shiftId and name=:shiftName",nativeQuery = true)
    List<OperatorShiftDetail> getOperatorShiftByShiftName(@Param("shiftId") Long shiftId,@Param("shiftName") String shiftName);
}
