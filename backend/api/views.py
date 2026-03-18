from django.shortcuts import render
from rest_framework import viewsets

from .models import AnnualGoal, Author, Genre, Book, Reading, ReadingSession
from .serializers import (
    AnnualGoalSerializer,
    AuthorSerializer,
    GenreSerializer,
    BookSerializer,
    ReadingSerializer,
    ReadingSessionSerializer
)

class BookViewSet(viewsets.ModelViewSet):
    queryset = Book.objects.all()
    serializer_class = BookSerializer

class AnnualGoalViewSet(viewsets.ModelViewSet):
    queryset = AnnualGoal.objects.all()
    serializer_class = AnnualGoalSerializer

class AuthorViewSet(viewsets.ModelViewSet):
    queryset = Author.objects.all()
    serializer_class = AuthorSerializer

class GenreViewSet(viewsets.ModelViewSet):
    queryset = Genre.objects.all()
    serializer_class = GenreSerializer

class ReadingViewSet(viewsets.ModelViewSet):
    queryset = Reading.objects.all()
    serializer_class = ReadingSerializer

class ReadingSessionViewSet(viewsets.ModelViewSet):
    queryset = ReadingSession.objects.all()
    serializer_class = ReadingSessionSerializer



