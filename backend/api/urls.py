from django.urls import path, include
from .views import (
    books,
    book_by_id,
    annual_goals,
    annual_goal_by_id,
    authors,
    author_by_id,
    genres,
    genre_by_id,
    readings,
    reading_by_id,
    reading_sessions,
    reading_session_by_id,
    stats,
    me
)



urlpatterns = [
    path('books/', books),
    path('books/<int:id>/', book_by_id),

    path('annual_goals/', annual_goals),
    path('annual_goals/<int:id>/', annual_goal_by_id),

    path('authors/', authors),
    path('authors/<int:id>/', author_by_id),

    path('genres/', genres),
    path('genres/<int:id>/', genre_by_id),

    path('readings/', readings),
    path('readings/<int:id>/', reading_by_id),

    path('reading_sessions/', reading_sessions),
    path('reading_sessions/<int:id>/', reading_session_by_id),

    path('stats/', stats),

    path('me/', me)
]