package com.example.schoolmnt.sm.post;

import com.example.schoolmnt.sm.appuser.AppUser;
import com.example.schoolmnt.sm.classes.Classes;
import com.example.schoolmnt.sm.exam.Exam;
import com.example.schoolmnt.sm.teacher.TeacherService;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class PostController {
    private PostService postService;
    private TeacherService teacherService;

    public PostController(PostService postService, TeacherService teacherService) {
        this.postService = postService;
        this.teacherService = teacherService;
    }

    @GetMapping("/post/posts")
    public String posts(Model model) {
        return postFindPaginated(1, "id", "asc", null, model);
    }

    @GetMapping("/post/new")
    public String newPost(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "post/postform";
    }

    @GetMapping("/post/new/{postid}")
    public String postFormWithClassid(@PathVariable Long postid, Model model) {
        model.addAttribute("post", postService.getPostById(postid));
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "post/postform";
    }

    @PostMapping("/post/new")
    public String createNewPost(@ModelAttribute("post")Post post, RedirectAttributes redirectAttributes) {
        postService.savePost(Post.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .lastmodified(LocalDateTime.now())
                .author(post.getAuthor())
                .build());
        redirectAttributes.addFlashAttribute("msg","Create new post successfully.");
        return "redirect:/post/posts";
    }

    @GetMapping("/post/edit/{id}")
    public String editPost(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.getPostById(id));
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "post/postform";
    }

    @PostMapping("/post/edit/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute("post") Post post, RedirectAttributes redirectAttributes) {
        Post newPost = postService.getPostById(id);
        newPost.setTitle(post.getTitle());
        newPost.setContent(post.getContent());
        newPost.setLastmodified(LocalDateTime.now());
        newPost.setAuthor(post.getAuthor());
        postService.savePost(newPost);
        redirectAttributes.addFlashAttribute("msg","Post update successfully.");
        return "redirect:/post/posts";
    }

    @GetMapping("/post/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/post/posts";
    }

    @GetMapping("/post/page/{pageNo}")
    public String postFindPaginated(@PathVariable int pageNo,
                                    @RequestParam("sortField") String sortField,
                                    @RequestParam("sortDir") String sortDir,
                                    @RequestParam(value = "keyword", required = false) String keyword,
                                    Model model) {
        int pageSize = 5;
        Page<Post> page = postService.findPaginated(pageNo, pageSize, sortField, sortDir, keyword);
        List<Post> posts = page.getContent();
        List<Post> modifiablePostList = new ArrayList<>(posts);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            AppUser user = (AppUser) auth.getPrincipal();
            modifiablePostList.removeIf(post -> !post.getAuthor().getEmail().equals(user.getEmail()));
        }


        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);

        model.addAttribute("posts", modifiablePostList);

        return "post/posts";
    }

    @GetMapping("/post/detail/{postid}")
    public String postDetail(@PathVariable Long postid, Model model) {
        Post post = postService.getPostById(postid);
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        Node document = parser.parse(post.getContent());
        String htmlContent = renderer.render(document);
        post.setContent(htmlContent);
        model.addAttribute("post", post);
        return "post/detail";
    }



}
