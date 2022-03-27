package jpabook.jpashop.api;

import jpabook.jpashop.Service.MemberService;
import jpabook.jpashop.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;
    /*
    * 조회 V1: 응답 값으로 엔티티를 직접 외부에 노출한다.
    * 문제점 :
    * - 엔티티에 프레젠테이션 계층(컨트롤러)을 위한 로직이 추가된다.
    * - 응답 스펙을 맞추기 위해 로직이 추가된다. (@JsonIgnore, 별도의 뷰 로직 등등)
    * - 실무에서는 같은 엔티티에 대해 API가 용도에 따라 다양하게 만들어지는데,
    *   한 엔티티에 각각의 API를 위한 프레젠테이션 응답 로직을 담기는 어렵다.
    * - 엔티티가 변경되면 API 스펙이 변한다.
    * - 추가로 컬렉션을 직접 반환하면 향후 API 스펙을 변경하기 어렵다.
    *   (별도의 Result 클래스 생성으로 해결)
    * 결론 :
    * - API 응답 스펙에 맞추어 별도의 DTO를 반환한다.
    * */
    // 조회 V1: 안 좋은 버전, 모든 엔티티가 노출된다.
    // @JsonIgnore -> 이건 정말 최악, api가 이거 하나인가! 화면에 종속적이지 마라!!!
    @GetMapping("/api/v1/members")
    public List<Member> membersV1 (){
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result membersV2 (){
        List<Member> findMembers = memberService.findMembers();
        // 엔티티 -> DTO 변환
        List<MemberDTO> collect = findMembers.stream()
                .map(m -> new MemberDTO(m.getName()))
                .collect(Collectors.toList());
        return new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDTO {
        private String name;
    }


    /*
     * 등록 V1: 요청 값으로 Member 엔티티를 직접 받는다.
     * 문제점 :
     * - 엔티티에 프레젠테이션 계층(컨트롤러)을 위한 로직이 추가된다.
     * - 엔티티에 API 검증을 위한 로직이 들어간다. (@NotEmpty 등등)
     * - 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데,
     *   한 엔티티에 각각의 API를 위한 모든 요청 요구사항을 담기는 어렵다.
     * 결론 :
     * - API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받는다.
     * */
    @PostMapping("/api/v1/members")
    public createMemberResponse saveMember1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new createMemberResponse(id);
    }

    /*
    *  등록 V2: 요청 값으로 Member 엔티티 대신에 별도의 DTO를 받는다.
    * */
    @PostMapping("/api/v2/members")
    public createMemberResponse saveMember2(@RequestBody @Valid createMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new createMemberResponse(id);
    }

    @Data
    static class createMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    static class createMemberResponse {
        private Long id;
        public createMemberResponse(Long id) {
            this.id = id;
        }
    }

    @PutMapping("/api/v2/members/{id}")
    public updateMemberResponse updateMember2(
            @PathVariable("id") Long id,
            @RequestBody @Valid updateMemberRequest request) {

        memberService.update(id, request.getName());
        Member member = memberService.findOne(id);
        return new updateMemberResponse(member.getId(), member.getName());
    }

    @Data
    static class updateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class updateMemberResponse {
        private Long id;
        private String name;
    }
}
